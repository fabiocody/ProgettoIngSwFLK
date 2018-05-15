package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.server.Methods;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String ip;
    private int port;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;
    private Queue<JsonObject> responsesBuffer;
    private final Object responsesBufferLock = new Object();
    private Queue<JsonObject> updatesBuffer;
    private final Object updatesBufferLock = new Object();
    private Thread recvThread;

    // FIELDS CONSTANTS
    private static final String method = "method";

    private Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.jsonParser = new JsonParser();
        this.responsesBuffer = new ConcurrentLinkedQueue<>();
        this.updatesBuffer = new ConcurrentLinkedQueue<>();
        this.startClient();
    }

    private JsonObject pollResponsesBuffer() {
        debug("pollResponsesBuffer called");
        synchronized (responsesBufferLock) {
            while (responsesBuffer.peek() == null) {
                try {
                    debug("Waiting on pollResponseBuffer");
                    responsesBufferLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            debug("Returning from pollResponsesBuffer");
            return responsesBuffer.poll();
        }
    }

    private JsonObject pollUpdatesBuffer() {
        debug("pollUpdatesBuffer called");
        synchronized (updatesBufferLock) {
            while (updatesBuffer.peek() == null) {
                try {
                    debug("Waiting on pollUpdatesBuffer");
                    updatesBufferLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            debug("Returning from pollUpdatesBuffer");
            return updatesBuffer.poll();
        }
    }

    private void startClient() {
        try {
            this.socket = new Socket(ip, port);
            this.socket.setKeepAlive(true);
            debug("Connection established");

            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            recvThread = new Thread(this::recv);
            recvThread.setDaemon(true);
            recvThread.start();

            // Add Player
            this.addPlayer();

            // Register for timer
            this.subscribeToWRTimer();

            // Wait for game to start (get timer tick)
            this.waitForGameToStart();

        } catch (IOException e) {
            error("Connection failed");
        } finally {
            try {
                this.in.close();
                this.out.close();
                this.socket.close();
            } catch (IOException e) {
                error("Closing");
            }
        }
    }

    private void debug(String message) {
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
                    synchronized (responsesBufferLock) {
                        responsesBuffer.add(inputJson);
                        responsesBufferLock.notifyAll();
                    }
                    debug("Added " + inputJson + " to responsesBuffer");
                    break;
                case UPDATE_WAITING_PLAYERS:
                case WR_TIMER_TICK:
                case GAME_STARTED:
                case GAME_TIMER_TICK:
                case PLAYERS:
                case FINAL_SCORES:
                case PUBLIC_OBJECTIVE_CARDS:
                case TOOL_CARDS:
                case FAVOR_TOKENS:
                case WINDOW_PATTERN:
                case ROUND_TRACK_DICE:
                case DRAFT_POOL:
                    synchronized (updatesBufferLock) {
                        updatesBuffer.add(inputJson);
                        updatesBufferLock.notifyAll();
                    }
                    debug("Added " + inputJson + " to updatesBuffer");
                    break;
            }
        }
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            error("DISCONNECTED");
            System.exit(1);
        }
        return line;
    }

    private String input(String prompt) throws IOException {
        System.out.print(prompt + " ");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line = stdin.readLine();
        stdin.close();
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
        JsonObject input = this.pollResponsesBuffer();
        this.uuid = UUID.fromString(input.get("UUID").getAsString());
        debug("INPUT " + input);
    }

    private void subscribeToWRTimer() {
        JsonObject payload = new JsonObject();
        payload.addProperty("playerID", this.uuid.toString());
        payload.addProperty(method, "subscribeToWRTimer");
        out.println(payload.toString());
        debug("INPUT " + this.pollResponsesBuffer());
    }

    private void waitForGameToStart() {
        JsonObject input;
        do {
            input = this.pollUpdatesBuffer();
            debug(input.toString());
            if (input.get(method).getAsString().equals("wrTimerTick"))
                debug("Timer tick " + input.get("tick").getAsInt());
            // TODO Update waiting players
        } while (input.get(method).getAsString().equals("wrTimerTick"));
        if (input.get(method).getAsString().equals("gameStarted"))
            debug("Game started");
    }

    public static void main(String[] args) {
        /*Scanner stdin = new Scanner(System.in);
        System.out.print("IP >>> ");
        String ip = stdin.nextLine();*/
        new Client("127.0.0.1", 42000);
    }
}