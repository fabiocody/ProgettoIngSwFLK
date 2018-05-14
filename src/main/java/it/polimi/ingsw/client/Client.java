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

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.jsonParser = new JsonParser();
        this.startClient();
    }

    public void startClient() {
        try (Socket socket = this.socket = new Socket(ip, port);
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true);
             Scanner stdin = this.stdin = new Scanner(System.in)) {
            System.out.println("Connection established!!");

            // Add Player
            this.addPlayer();

            // Register for timer
            this.subscribeToWRTimer();

            // Wait for game to start (get timer tick)
            this.waitForGameToStart();

        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }

    private synchronized String readLine() throws IOException {
        String line;
        while ((line = in.readLine()) == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        payload.addProperty("method", "addPlayer");
        System.out.println(payload.toString());
        out.println(payload.toString());
        JsonObject input = this.parseJson(this.readLine());
        this.uuid = UUID.fromString(input.get("UUID").getAsString());
        System.out.println(input);
    }

    private void subscribeToWRTimer() throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("playerID", this.uuid.toString());
        payload.addProperty("method", "registerWRTimer");
        out.println(payload.toString());
        System.out.println(this.readLine());
    }

    private void waitForGameToStart() throws IOException {
        JsonObject input;
        do {
            input = this.parseJson(this.readLine());
            System.out.println(input);
            if (input.get("msgType").equals("wrTimerTick"))
                System.out.println(input.get("tick").getAsInt());
            // TODO Update waiting players
        } while (input.get("msgType").getAsString().equals("wrTimerTick"));
        if (input.get("msgType").getAsString().equals("gameStarted"))
            System.out.println("Game started");
    }

    public static void main(String[] args) {
        /*Scanner stdin = new Scanner(System.in);
        System.out.print("IP >>> ");
        String ip = stdin.nextLine();*/
        new Client("127.0.0.1", 42000);
    }
}