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
    private boolean run = true;

    ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    void showDisconnectedUserMessage() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        Logger.log("Disconnected " + address + " (nickname: " + getNickname() + ")");
        run = false;
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
                    onUserDisconnection();
                    return;
                }
                Logger.debug("Received: " + input.toString());
                // UUID validation
                if (!input.get(JsonFields.METHOD).getAsString().equals(Methods.ADD_PLAYER.getString()) &&
                        !input.get(JsonFields.PLAYER_ID).getAsString().equals(getUuid().toString())) {
                    Logger.error("AUTH ERROR");
                    continue;
                }
                Methods calledMethod;
                try {
                    calledMethod = Methods.fromString(input.get(JsonFields.METHOD).getAsString());
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
                    case CANCEL_TOOL_CARD_USAGE:
                        this.cancelToolCardUsage(input);
                        break;
                    case PROBE:
                        setProbed();
                        break;
                    default:
                        Logger.error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (Exception e) {
            Logger.error("run");
            Logger.printStackTraceConditionally(e);
            this.onUserDisconnection();
        }
    }

    private String readLine() throws IOException {
        return in.readLine();
    }

    private JsonObject parseJson(String string) {
        Logger.debug("Parsing JSON");
        return getJsonParser().parse(string).getAsJsonObject();
    }

    private void addPlayer(JsonObject input) {
        Logger.debug("addPlayer called");
        setUuid(null);
        setNickname(null);
        Logger.debugInput(input);
        String tempNickname = input.get(JsonFields.NICKNAME).getAsString();
        try {
            setUuid(WaitingRoomController.getInstance().addPlayer(tempNickname));
            setNickname(tempNickname);
            Logger.log(getNickname() + " logged in successfully (" + getUuid() + ")");
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, false);
            payload.addProperty(JsonFields.PLAYER_ID, getUuid().toString());
            JsonArray waitingPlayers = new JsonArray();
            for (String p : WaitingRoomController.getInstance().getWaitingPlayers())
                waitingPlayers.add(p);
            payload.add(JsonFields.PLAYERS, waitingPlayers);
            Logger.debugPayload(payload);
            out.println(payload.toString());
            startProbeThread();
        } catch (LoginFailedException e) {
            Logger.log("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, false);
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (NicknameAlreadyUsedInGameException e) {
            setGameController(e.getController());
            getGameController().addNetwork(this);
            setNickname(tempNickname);
            Player player = getGameController().getGame().getPlayer(getNickname());
            setUuid(player.getId());
            getGameController().unsuspendPlayer(getUuid());
            Logger.log(getNickname() + " logged back in (" + getUuid() + ")");
            JsonObject payload = new JsonObject();
            payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
            payload.addProperty(JsonFields.LOGGED, true);
            payload.addProperty(JsonFields.RECONNECTED, true);
            payload.addProperty(JsonFields.PLAYER_ID, getUuid().toString());
            payload.add(JsonFields.PRIVATE_OBJECTIVE_CARD, createObjectiveCardJson(player.getPrivateObjectiveCard()));
            Logger.debugPayload(payload);
            out.println(payload.toString());
            startProbeThread();
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
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.CHOOSE_PATTERN.getString());
        try {
            getGameController().choosePattern(getUuid(), patternIndex);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.log(getNickname() + " has chosen pattern " + patternIndex);
        } catch (IndexOutOfBoundsException e) {
            payload.addProperty(JsonFields.RESULT, false);
            Logger.log("Pattern choosing for " + getNickname() + " unsuccessful");
        }
        Logger.debugPayload(payload);
        out.println(payload.toString());
    }

    private void placeDie(JsonObject input) {
        int draftPoolIndex = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int x = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_X).getAsInt();
        int y = input.get(JsonFields.ARG).getAsJsonObject().get(JsonFields.TO_CELL_Y).getAsInt();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.PLACE_DIE.getString());
        try {
            getGameController().placeDie(getUuid(), draftPoolIndex, x, y);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.log(getNickname() + " placed a die");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE,e.getMessage());
            Logger.log(getNickname() + " die placement was refused");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        }
    }

    private void requiredData(JsonObject input) {
        int cardIndex = input.get(JsonFields.CARD_INDEX).getAsInt();
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        JsonObject payload = getGameController().requiredData(cardIndex,id);
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
            getGameController().useToolCard(id, cardIndex, data);
            payload.addProperty(JsonFields.RESULT, true);
            Logger.log(getNickname() + " used a tool card");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        } catch (InvalidEffectArgumentException | InvalidEffectResultException e) {
            payload.addProperty(JsonFields.RESULT, false);
            payload.addProperty(JsonFields.ERROR_MESSAGE, e.getMessage());
            Logger.log(getNickname() + " usage of tool card was refused");
            Logger.debugPayload(payload);
            out.println(payload.toString());
        }
    }

    private void cancelToolCardUsage(JsonObject input) {
        UUID id = UUID.fromString(input.get(JsonFields.PLAYER_ID).getAsString());
        int cardIndex = input.getAsJsonObject(JsonFields.ARG).get(JsonFields.CARD_INDEX).getAsInt();
        getGameController().cancelToolCardUsage(id, cardIndex);
    }

    private void nextTurn() {
        getGameController().nextTurn();
        Logger.log(getNickname() + " has ended his turn");
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
