package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.server.*;
import joptsimple.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader stdin;
    private String ip;
    private int port;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;
    private Queue<JsonObject> responseBuffer;
    private final Object responseBufferLock = new Object();
    private final Object gameStartedLock = new Object();
    private Thread recvThread;

    // FLAGS
    private boolean debugActive;
    private boolean logged = false;
    private boolean gameStarted = false;

    // FIELDS CONSTANTS
    private static final String method = "method";

    private Client(String ip, int port, boolean debug) {
        this.ip = ip;
        this.port = port;
        this.debugActive = debug;
        this.jsonParser = new JsonParser();
        this.responseBuffer = new ConcurrentLinkedQueue<>();
        this.startClient();
    }

    private JsonObject pollResponseBuffer() {
        debug("pollResponsesBuffer called");
        synchronized (responseBufferLock) {
            while (responseBuffer.peek() == null) {
                try {
                    debug("Waiting on pollResponseBuffer");
                    responseBufferLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            debug("Returning from pollResponsesBuffer");
            return responseBuffer.poll();
        }
    }

    private void startClient() {
        try {
            this.socket = new Socket(ip, port);
            this.socket.setKeepAlive(true);
            log("Connection established");

            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.stdin = new BufferedReader(new InputStreamReader(System.in));

            recvThread = new Thread(this::recv);
            recvThread.setDaemon(true);
            recvThread.start();

            // Add Player
            while (!logged) this.addPlayer();

            // Wait for game to start (get timer tick)
            synchronized (this.gameStartedLock) {
                while (!gameStarted) gameStartedLock.wait();
            }

            log("GAME STARTED");

            recvThread.interrupt();

        } catch (IOException e) {
            error("Connection failed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.in != null) this.in.close();
                if (this.out != null) this.out.close();
                if (this.socket != null) this.socket.close();
            } catch (IOException e) {
                error("Closing");
            }
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void debug(String message) {
        if (this.debugActive)
            System.out.println("[DEBUG] " + message);
    }

    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    private void recv() {
        boolean run = true;
        while (run) {
            JsonObject inputJson;
            try {
                inputJson = this.jsonParser.parse(this.readLine()).getAsJsonObject();
                debug(inputJson.toString());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Methods recvMethod;
            try {
                recvMethod = Methods.getAsMethods(inputJson.get(method).getAsString());
            } catch (NoSuchElementException e) {
                error("METHOD NOT RECOGNIZED");
                continue;
            }
            debug("Received method " + recvMethod.getString());
            switch (recvMethod) {
                case ADD_PLAYER:
                case SUBSCRIBE_TO_WR_TIMER:
                case SUBSCRIBE_TO_GAME_TIMER:
                case CHOOSE_PATTERN:
                case NEXT_TURN:
                case PLACE_DIE:
                case USE_TOOL_CARD:
                    synchronized (responseBufferLock) {
                        responseBuffer.add(inputJson);
                        responseBufferLock.notifyAll();
                        debug("Added " + inputJson + " to responsesBuffer");
                    }
                    break;
                case UPDATE_WAITING_PLAYERS:
                    this.updateWaitingPlayers(inputJson);
                    break;
                case WR_TIMER_TICK:
                    this.wrTimerTick(inputJson);
                    break;
                case GAME_STARTED:
                    synchronized (gameStartedLock) {
                        gameStarted = true;
                        gameStartedLock.notifyAll();
                    }
                    break;
                case GAME_TIMER_TICK:
                case PLAYERS:
                case FINAL_SCORES:
                case PUBLIC_OBJECTIVE_CARDS:
                case TOOL_CARDS:
                case FAVOR_TOKENS:
                case WINDOW_PATTERN:
                case ROUND_TRACK_DICE:
                case DRAFT_POOL:
                    break;
            }
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            error("DISCONNECTED");
            System.exit(1); // TODO Handle
        }
        return line;
    }

    private String input(String prompt) throws IOException {
        System.out.print(prompt + " ");
        String line = stdin.readLine();
        return line;
    }

    private void addPlayer() throws IOException {
        debug("addPlayer called");
        this.nickname = this.input("Nickname >>>");
        JsonObject payload = new JsonObject();
        payload.addProperty("nickname", this.nickname);
        payload.addProperty(method, "addPlayer");
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        JsonObject input = this.pollResponseBuffer();
        logged = input.get("logged").getAsBoolean();
        debug("" + logged);
        if (logged) {
            log("Login successful");
            this.uuid = UUID.fromString(input.get("UUID").getAsString());
            debug("INPUT " + input);
            JsonArray players = input.get("players").getAsJsonArray();
            debug("SIZE: " + players.size());
            if (players.size() < 4)
                this.subscribeToWRTimer();
            log(players.toString());
        } else {
            log("Login failed");
        }
    }

    private void subscribeToWRTimer() {
        JsonObject payload = new JsonObject();
        payload.addProperty("playerID", this.uuid.toString());
        payload.addProperty(method, "subscribeToWRTimer");
        out.println(payload.toString());
        debug("INPUT " + this.pollResponseBuffer());
    }

    private void updateWaitingPlayers(JsonObject input) {
        log(input.get("waitingPlayers").getAsJsonArray().toString());
    }

    private void wrTimerTick(JsonObject input) {
        log(String.valueOf(input.get("tick").getAsInt()));
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("debug");
        parser.accepts("ip").withRequiredArg();
        try {
            OptionSet options = parser.parse(args);
            String ip;
            if (options.has("ip")) {
                ip = (String) options.valueOf("ip");
            } else {
                Scanner stdin = new Scanner(System.in);
                System.out.print("IP >>> ");
                ip = stdin.nextLine();
            }
            new Client(ip, 42000, options.has("debug"));
        } catch (OptionException e) {
            System.out.println("usage: sagradaclient [--debug] [--ip IP]");
        }
    }
}