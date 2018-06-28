package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.shared.util.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class ServerSocketHandler extends ServerNetwork implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonParser jsonParser;
    private boolean run = true;

    public ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
        this.jsonParser = new JsonParser();
    }

    @Override
    void showDisconnectedUserMessage() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        Logger.log("Disconnected " + address + " (nickname: " + this.nickname + ")");
        run = false;
        Thread.currentThread().interrupt();
    }

    @Override
    void close() {
        try {
            if (this.in != null) this.in.close();
            if (this.out != null) this.out.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException e) {
            Logger.error("Error closing ServerNetwork");
        }
    }

    // REQUESTS HANDLER

    @Override
    public void run() {
        try (Socket socket = this.clientSocket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true)) {
            Logger.log("Connected to " + socket.getInetAddress() + ":" + socket.getPort() + " via socket");
            while (this.run) {
                JsonObject input;
                try {
                    input = this.parseJson(this.readLine());
                } catch (NullPointerException e) {
                    Logger.debug("JSON parsing failed");
                    continue;
                }
                Logger.debug("Received: " + input.toString());
                // UUID validation
                if (!input.get(JsonFields.METHOD).getAsString().equals(Methods.ADD_PLAYER.getString()) &&
                        !input.get(JsonFields.PLAYER_ID).getAsString().equals(this.uuid.toString())) {
                    Logger.error("AUTH ERROR");
                    continue;
                }
                Methods calledMethod;
                try {
                    calledMethod = Methods.getAsMethods(input.get(JsonFields.METHOD).getAsString());
                } catch (NoSuchElementException e) {
                    Logger.error("METHOD NOT RECOGNIZED");
                    continue;
                }
                Logger.debug("Received method " + calledMethod.getString());
                switch (calledMethod) {
                    case ADD_PLAYER:
                        this.addPlayer(input);
                        break;
                    case CHOOSE_PATTERN:
                        this.choosePattern(input);
                        break;
                    case NEXT_TURN:
                        this.nextTurn();
                        break;
                    case PLACE_DIE:
                        this.placeDie(input);
                        break;
                    case USE_TOOL_CARD:
                        this.useToolCard(input);
                        break;
                    case REQUIRED_DATA:
                        this.requiredData(input);
                        break;
                    case PROBE:
                        probed = true;
                        break;
                    default:
                        Logger.error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (Exception e) {
            Logger.error("run");
            this.onUserDisconnection();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.onUserDisconnection();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
        }
        return line;
    }

    private JsonObject parseJson(String string) {
        Logger.debug("Parsing JSON");
        return this.jsonParser.parse(string).getAsJsonObject();
    }

    private void addPlayer(JsonObject input) {
        Logger.debug("addPlayer called");
        Logger.debug("INPUT " + input.toString());
        String tempNickname = input.get(JsonFields.NICKNAME).getAsString();
        try {
            this.uuid = WaitingRoomController.getInstance().addPlayer(tempNickname);
            this.nickname = tempNickname;
            Logger.log(this.nickname + " logged in successfully (" + this.uuid + ")");
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, false);
            payload.addProperty(JsonFields.PLAYER_ID, this.uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (String p : WaitingRoomController.getInstance().getWaitingPlayers())
                waitingPlayers.add(p);
            payload.add(JsonFields.PLAYERS, waitingPlayers);
            Logger.debugPayload(payload);
            out.println(payload.toString());
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            Logger.log("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, false);
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (NicknameAlreadyUsedInGameException e) {
            Game game = e.getGame();
            Optional<GameController> optionalGameController = SagradaServer.getInstance().getGameControllers().stream()
                    .filter(controller -> controller.getGame() == game)
                    .findFirst();
            this.gameController = optionalGameController.orElse(null);
            this.nickname = tempNickname;
            Player player = game.getPlayer(this.nickname);
            this.uuid = player.getId();
            Logger.log(this.nickname + " logged back in (" + this.uuid + ")");
            //game.addObserver(this);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, true);
            payload.addProperty(JsonFields.PLAYER_ID, this.uuid.toString());
            Logger.debugPayload(payload);
            out.println(payload.toString());
            this.probeThread = new Thread(this::probeCheck);
            this.probeThread.start();
            gameController.unsuspendPlayer(this.uuid);
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    fullUpdate();
                }
            }, 500);
        }
    }

    private void choosePattern(JsonObject input) {
        int patternIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.PATTERN_INDEX).getAsInt();
        this.gameController.choosePattern(this.uuid, patternIndex);
        Logger.log(this.nickname + " has chosen pattern " + patternIndex);
    }

    private void placeDie(JsonObject input) {
        int draftPoolIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int x = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_X).getAsInt();
        int y = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_Y).getAsInt();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLACE_DIE.getString());
        try {
            this.gameController.placeDie(this.uuid, draftPoolIndex, x, y);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.log(this.nickname + " placed a die");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE,e.getMessage());
            Logger.log(this.nickname + " die placement was refused");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        }
    }

    private void requiredData(JsonObject input) {
        int cardIndex = input.get(JsonFields.CARD_INDEX).getAsInt();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        JsonObject payload = this.gameController.requiredData(cardIndex,id);
        Logger.debugPayload(payload);
        out.println(payload.toString());
    }

    private void useToolCard(JsonObject input) {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, JsonFields.USE_TOOL_CARD);
        int cardIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.CARD_INDEX).getAsInt();
        JsonObject data = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DATA).getAsJsonObject();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        data.addProperty(JsonFields.PLAYER_ID, id.toString());
        try {
            this.gameController.useToolCard(id, cardIndex, data);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.log(this.nickname + " used a tool card");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE, e.getMessage());
            Logger.log(this.nickname + " usage of tool card was refused");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        }
    }

    private void nextTurn() {
        this.gameController.nextTurn();
        Logger.log(this.nickname + " has ended his turn");
    }

    @Override
    JsonObject updatePlayersList() {
        JsonObject payload = super.updatePlayersList();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateToolCards() {
        JsonObject payload = super.updateToolCards();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject sendPublicObjectiveCards() {
        JsonObject payload = super.sendPublicObjectiveCards();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateWindowPatterns() {
        JsonObject payload = super.updateWindowPatterns();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateFavorTokens() {
        JsonObject payload = super.updateFavorTokens();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateDraftPool() {
        JsonObject payload = super.updateDraftPool();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateRoundTrack() {
        JsonObject payload = super.updateRoundTrack();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject turnManagement() {
        JsonObject payload = super.turnManagement();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateFinalScores() {
        JsonObject payload = super.updateFinalScores();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateTimerTick(Methods method, String tick) {
        JsonObject payload = super.updateTimerTick(method, tick);
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject updateWaitingPlayers() {
        JsonObject payload = super.updateWaitingPlayers();
        out.println(payload.toString());
        return payload;
    }

    @Override
    JsonObject setupGame() {
        JsonObject payload = super.setupGame();
        out.println(payload.toString());
        return payload;
    }

    @Override
    void sendProbe() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PROBE.getString());
        out.println(payload.toString());
    }
}
