package it.polimi.ingsw.client;

import com.google.gson.*;
import java.io.*;
import java.net.Socket;
import java.util.*;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner stdin;
    private String ip;
    private int port;
    private String nickname;
    private UUID uuid;
    private JsonParser jsonParser;

    private static final String method = "method";

    private Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.jsonParser = new JsonParser();
        this.startClient();
    }

    private void startClient() {
        try (Socket socket = this.socket = new Socket(ip, port);
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true);
             Scanner stdin = this.stdin = new Scanner(System.in)) {
            socket.setKeepAlive(true);
            debug("Connection established");

            // Add Player
            this.addPlayer();

            // Register for timer
            this.subscribeToWRTimer();

            // Wait for game to start (get timer tick)
            this.waitForGameToStart();

        } catch (IOException e) {
            debug("Connection failed");
        }
    }

    private void debug(String string) {
        System.err.println("[DEBUG] " + string);
    }

    private String readLine() throws IOException {
        String line = in.readLine();
        if (line == null) {
            debug("DISCONNECTED");
            System.exit(1);
        }
        return line;
    }

    private JsonObject parseJson(String string) {
        return this.jsonParser.parse(string).getAsJsonObject();
    }

    private void addPlayer() throws IOException {
        System.out.print("Nickname >>> ");
        this.nickname = stdin.nextLine();
        JsonObject payload = new JsonObject();
        payload.addProperty("nickname", this.nickname);
        payload.addProperty(method, "addPlayer");
        debug("PAYLOAD " + payload.toString());
        out.println(payload.toString());
        JsonObject input = this.parseJson(this.readLine());
        this.uuid = UUID.fromString(input.get("UUID").getAsString());
        debug("INPUT " + input);
    }

    private void subscribeToWRTimer() throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("playerID", this.uuid.toString());
        payload.addProperty(method, "subscribeToWRTimer");
        out.println(payload.toString());
        debug("INPUT " + this.readLine());
    }

    private void waitForGameToStart() throws IOException {
        JsonObject input;
        do {
            input = this.parseJson(this.readLine());
            debug(input.toString());
            if (input.get(method).equals("wrTimerTick"))
                System.out.println(input.get("tick").getAsInt());
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