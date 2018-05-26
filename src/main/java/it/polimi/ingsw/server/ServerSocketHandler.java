package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.model.toolcards.ToolCard;
import it.polimi.ingsw.util.CountdownTimer;
import java.io.*;
import java.net.*;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {
    // Observes CountdownTimer (from WaitingRoom and TurnManager), WaitingRoom and Game

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private WaitingRoomEndPoint waitingRoomEndPoint;
    private GameEndPoint gameEndPoint;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;
    private Game game;
    private boolean run = true;
    private Thread probeThread;
    private boolean probed = true;

    // FIELDS CONSTANTS
    private static final String method = "method";
    private static final String playerID = "playerID";

    public ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
        /*try {
            this.clientSocket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }*/
        this.jsonParser = new JsonParser();
        this.waitingRoomEndPoint = new WaitingRoomEndPoint();
        waitingRoomEndPoint.subscribeToWaitingRoom(this);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void debug(String message) {
        if (SagradaServer.getInstance().isDebugActive())
            System.out.println("[DEBUG] " + message);
    }

    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    private void probe() {
        while (42 == 42) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                break;
            }
            if (!probed) {
                error("Probe error");
                notifyDisconnectedUser();
                Thread.currentThread().interrupt();
                waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
                waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
            }
            JsonObject payload = new JsonObject();
            payload.addProperty(method, "probe");
            out.println(payload.toString());
            probed = false;
        }
    }

    private void notifyDisconnectedUser() {
        String address = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        log("Disconnected " + address + " (nickname: " + nickname + ")");
        waitingRoomEndPoint.removePlayer(nickname);
        //game.getTurnManager().suspendPlayer(nickname);
        run = false;
        Thread.currentThread().interrupt();
    }

    // REQUESTS HANDLER

    @Override
    public void run() {
        try (Socket socket = this.clientSocket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true)) {
            log("Connected to " + socket.getInetAddress() + ":" + socket.getPort() + " via socket");
            while (this.run) {
                JsonObject input;
                try {
                    input = this.parseJson(this.readLine());
                } catch (NullPointerException e) {
                    debug("JSON parsing failed");
                    continue;
                }
                debug("Received: " + input.toString());
                // UUID validation
                if (!input.get(method).getAsString().equals(Methods.ADD_PLAYER.getString()) &&
                        !input.get(playerID).getAsString().equals(this.uuid.toString())) {
                    error("AUTH ERROR");
                    continue;
                }
                Methods calledMethod;
                try {
                    calledMethod = Methods.getAsMethods(input.get(method).getAsString());
                } catch (NoSuchElementException e) {
                    error("METHOD NOT RECOGNIZED");
                    continue;
                }
                debug("Received method " + calledMethod.getString());
                switch (calledMethod) {
                    case ADD_PLAYER:
                        this.addPlayer(input);
                        break;
                    case SUBSCRIBE_TO_WR_TIMER:
                        this.subscribeToWRTimer();
                        break;
                    case SUBSCRIBE_TO_GAME_TIMER:
                        // TODO
                        break;
                    case CHOOSE_PATTERN:
                        this.choosePattern(input);
                        break;
                    case NEXT_TURN:
                        // TODO
                        break;
                    case PLACE_DIE:
                        // TODO
                        break;
                    case USE_TOOL_CARD:
                        // TODO
                        break;
                    case PROBE:
                        probed = true;
                        break;
                    default:
                        error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (Exception e) {
            error("run");
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.notifyDisconnectedUser();
            this.probeThread.interrupt();
            Thread.currentThread().interrupt();
            waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
            waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
        }
        return line;
    }

    private JsonObject parseJson(String string) {
        debug("Parsing JSON");
        return this.jsonParser.parse(string).getAsJsonObject();
    }

    private void addPlayer(JsonObject input) {
        debug("addPlayer called");
        debug("INPUT " + input.toString());
        String tempNickname = input.get("nickname").getAsString();
        try {
            uuid = this.waitingRoomEndPoint.addPlayer(tempNickname);
            nickname = tempNickname;
            log(nickname + " logged in (" + uuid + ")");
            debug("UUID: " + uuid.toString());
            JsonObject payload = new JsonObject();
            payload.addProperty(method, "addPlayer");
            payload.addProperty("logged", true);
            payload.addProperty("UUID", uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (Player p : this.waitingRoomEndPoint.getWaitingPlayers())
                waitingPlayers.add(p.getNickname());
            payload.add("players", waitingPlayers);
            out.println(payload.toString());
            debug("PAYLOAD " + payload.toString());
            this.probeThread = new Thread(this::probe);
            this.probeThread.start();
        } catch (LoginFailedException e) {
            log("Login failed for nickname " + tempNickname);
            JsonObject payload = new JsonObject();
            payload.addProperty(method, "addPlayer");
            payload.addProperty("logged", false);
            out.println(payload.toString());
            debug("PAYLOAD " + payload.toString());
        }
    }

    private void subscribeToWRTimer() {
        this.waitingRoomEndPoint.subscribeToWaitingRoomTimer(this);
        debug("Timer registered");
        JsonObject payload = new JsonObject();
        payload.addProperty(method, "subscribeToWRTimer");
        payload.addProperty("result", true);
        out.println(payload.toString());
    }

    private void choosePattern(JsonObject input) {
        int patternIndex = input.get("arg").getAsJsonObject().get("patternIndex").getAsInt();
        UUID id = UUID.fromString(input.get("playerID").getAsString());
        this.gameEndPoint.choosePattern(id, patternIndex);
        log(nickname + " has chosen pattern " + patternIndex);
    }

    private void updatePlayersList() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "players");
        JsonArray playersJSON = new JsonArray();
        for (String nickname : this.gameEndPoint.getCurrentPlayers()) {
            playersJSON.add(nickname);
        }
        payload.add("players", playersJSON);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateToolCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "toolCards");
        JsonArray cards = new JsonArray();
        for (ToolCard card : this.gameEndPoint.getToolCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty("name", card.getName());
            jsonCard.addProperty("description", card.getDescription());
            jsonCard.addProperty("used", card.isUsed());
            cards.add(jsonCard);
        }
        payload.add("cards", cards);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void sendPublicObjectiveCards() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "publicObjectiveCards");
        JsonArray cards = new JsonArray();
        for (ObjectiveCard card : this.gameEndPoint.getPublicObjectiveCards()) {
            JsonObject jsonCard = new JsonObject();
            jsonCard.addProperty("name", card.getName());
            jsonCard.addProperty("description", card.getDescription());
            jsonCard.addProperty("victoryPoints", card.getVictoryPoints());
            cards.add(jsonCard);
        }
        payload.add("cards", cards);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateWindowPatterns() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "windowPatterns");
        JsonObject windowPatterns = new JsonObject();
        for (String player : this.gameEndPoint.getCurrentPlayers()) {
            windowPatterns.add(player, createWindowPatternJSON(this.gameEndPoint.getWindowPatternOf(player)));
        }
        payload.add("windowPatterns", windowPatterns);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void updateDraftPool() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "draftPool");
        JsonArray dice = new JsonArray();
        for (Die d : this.gameEndPoint.getDraftPool()) {
            JsonObject die = new JsonObject();
            die.addProperty("color", d.getColor().toString());
            die.addProperty("value", d.getValue());
            die.addProperty("cliString", d.toString());
            dice.add(die);
        }
        payload.add("dice", dice);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    private void gameStarted() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "gameStarted");
        payload.addProperty("activePlayer", this.gameEndPoint.getActivePlayer());
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
    }

    // UPDATE HANDLER

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith("WaitingRoom")) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                debug("Timer tick (from update): " + tick);
                this.updateTimerTick(tick);
            }
            // TODO "tm <tick>"
        } else if (o instanceof WaitingRoom) {
            if (arg instanceof Game) {
                this.game = (Game) arg;
                this.game.addObserver(this);
                this.gameEndPoint = new GameEndPoint(game);
                waitingRoomEndPoint.unsubscribeFromWaitingRoom(this);
                waitingRoomEndPoint.unsubscribeFromWaitingRoomTimer(this);
                this.setupGame();
            } else if (arg instanceof List && uuid != null) {
                this.updateWaitingPlayersList((List<Player>) arg);
            }
        } else if (o instanceof Game) {
            updatePlayersList();
            updateToolCards();
            sendPublicObjectiveCards();
            updateWindowPatterns();
            updateDraftPool();
            gameStarted();
        }
    }

    private void updateTimerTick(int tick) {
        debug("updateTimerTick called");
        JsonObject payload = new JsonObject();
        payload.addProperty(method,"wrTimerTick");
        payload.addProperty("tick", tick);
        out.println(payload.toString());
        debug("Timer Tick Update sent");
    }

    private void updateWaitingPlayersList(List<Player> players) {
        debug("updateWaitingPlayersList called");
        JsonObject payload = new JsonObject();
        payload.addProperty(method, "updateWaitingPlayers");
        JsonArray array = new JsonArray();
        for (Player p : players) array.add(p.getNickname());
        payload.add("players", array);
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        debug("Update Waiting Players List sent");
    }

    private JsonObject createWindowPatternJSON(WindowPattern wp) {
        JsonObject wpJSON = new JsonObject();
        wpJSON.addProperty("difficulty", wp.getDifficulty());
        JsonArray grid = new JsonArray();
        for (Cell c : wp.getGrid()) {
            JsonObject cellJSON = new JsonObject();
            cellJSON.addProperty("cellColor", c.getCellColor() != null ? c.getCellColor().toString() : null);
            cellJSON.addProperty("cellValue", c.getCellValue());
            JsonObject die = null;
            if (c.getPlacedDie() != null) {
                die = new JsonObject();
                die.addProperty("color", c.getPlacedDie().getColor().toString());
                die.addProperty("value", c.getPlacedDie().getValue());
            }
            cellJSON.add("die", die);
            grid.add(cellJSON);
        }
        wpJSON.add("grid", grid);
        wpJSON.addProperty("cliString", wp.toString());
        return wpJSON;
    }

    private void setupGame() {
        debug("setupGame called");
        JsonObject payload = new JsonObject();
        payload.addProperty(method, "gameSetup");
        Player player = this.gameEndPoint.getPlayer(uuid);
        ObjectiveCard privateObjectiveCard = player.getPrivateObjectiveCard();

        // Private objective card
        JsonObject privateObjectiveCardJSON = new JsonObject();
        privateObjectiveCardJSON.addProperty("name", privateObjectiveCard.getName());
        privateObjectiveCardJSON.addProperty("description", privateObjectiveCard.getDescription());
        privateObjectiveCardJSON.addProperty("victoryPoints", privateObjectiveCard.getVictoryPoints());
        payload.add("privateObjectiveCard", privateObjectiveCardJSON);

        // Window Patterns
        List<WindowPattern> windowPatterns = player.getWindowPatternList();
        JsonArray windowPatternsJSON = new JsonArray();
        for (WindowPattern wp : windowPatterns) {
            windowPatternsJSON.add(createWindowPatternJSON(wp));
        }
        payload.add("windowPatterns", windowPatternsJSON);


        out.println(payload.toString());

        log("A game has started for " + nickname);

        this.gameEndPoint.getPlayer(uuid).addObserver(this);
    }

}
