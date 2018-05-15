package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.util.CountdownTimer;
import java.io.*;
import java.net.*;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {
    // Observes CountdownTimer (from WaitingRoom and TurnManager) and WaitingRoom

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;
    private Game game;
    private boolean run = true;

    // FIELDS CONSTANTS
    private static final String method = "method";
    private static final String playerID = "playerID";

    public ServerSocketHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.clientSocket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.jsonParser = new JsonParser();
        WaitingRoom.getInstance().addObserver(this);
    }

    private void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    // REQUESTS HANDLER

    @Override
    public synchronized void run() {
        try (Socket socket = this.clientSocket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true)) {
            debug("Connected with " + socket.getInetAddress() + ":" + socket.getPort());
            while (this.run) {
                JsonObject input = this.parseJson(this.readLine());
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
                        this.subscribeToWRTimer(input);
                        break;
                    case SUBSCRIBE_TO_GAME_TIMER:
                        // TODO
                        break;
                    case CHOOSE_PATTERN:
                        // TODO
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
                    default:
                        error("METHOD NOT RECOGNIZED");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            error("Client " + nickname + " (" + uuid + ") disconnected");
            if (game == null) {
                // TODO WaitingRoom.getInstance().getWaitingPlayers().remove()
            }
            Thread.currentThread().interrupt();
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
        nickname = input.get("nickname").getAsString();
        uuid = WaitingRoom.getInstance().addPlayer(nickname);
        debug("UUID: " + uuid.toString());
        JsonObject payload = new JsonObject();
        payload.addProperty(method, "addPlayer");
        payload.addProperty("logged", true);
        payload.addProperty("UUID", uuid.toString());
        JsonArray waitingPlayers = new JsonArray();
        for (Player p : WaitingRoom.getInstance().getWaitingPlayers())
            waitingPlayers.add(p.getNickname());
        payload.add("players", waitingPlayers);
        this.out.println(payload.toString());
        debug("PAYLOAD " + payload.toString());
    }

    private void subscribeToWRTimer(JsonObject input) {
        WaitingRoom.getInstance().getTimer().addObserver(this);
        debug("Timer registered");
        JsonObject payload = new JsonObject();
        payload.addProperty(method, input.get("method").getAsString());
        payload.addProperty("result", true);
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
                sendTickUpdate(tick);
            }
            // TODO "tm <tick>"
        } else if (o instanceof WaitingRoom) {
            this.game = (Game) arg;
            WaitingRoom.getInstance().deleteObserver(this);
            WaitingRoom.getInstance().getTimer().deleteObserver(this);
        }
    }

    private void sendTickUpdate(int tick) {
        JsonObject payload = new JsonObject();
        payload.addProperty(method,"wrTimerTick");
        payload.addProperty("tick", tick);
        out.println(payload.toString());
    }

}
