package it.polimi.ingsw.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.util.CountdownTimer;
import java.io.*;
import java.net.Socket;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {
    // Observes CountdownTimer (from WaitingRoom and TurnManager) and WaitingRoom

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UUID uuid;
    private JsonParser jsonParser;
    private Game game;

    public ServerSocketHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jsonParser = new JsonParser();
        WaitingRoom.getInstance().addObserver(this);
    }

    @Override
    public synchronized void run() {
        boolean run = true;
        try (Socket socket = this.socket;
             BufferedReader in = this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = this.out = new PrintWriter(socket.getOutputStream(), true);
             Scanner stdin = new Scanner(System.in)) {
            System.out.println("Connection established!!!");

            // Add Player
            JsonObject input = this.parseJson(this.readLine());
            System.out.println(input.toString());
            uuid = WaitingRoom.getInstance().addPlayer(input.get("nickname").getAsString());
            System.out.println("UUID: " + uuid.toString());
            JsonObject payload = new JsonObject();
            payload.addProperty("msgType", "addPlayer");
            payload.addProperty("logged", true);
            payload.addProperty("UUID", uuid.toString());
            JsonArray waitingPlayers = new JsonArray();
            for (Player p : WaitingRoom.getInstance().getWaitingPlayers())
                waitingPlayers.add(p.getNickname());
            payload.add("players", waitingPlayers);
            this.out.println(payload.toString());
            System.out.println(payload.toString());

            // Register for timer
            input = this.parseJson(this.readLine());
            // TODO Check UUID
            if (input.get("method").getAsString().equals("registerWRTimer")) {
                WaitingRoom.getInstance().getTimer().addObserver(this);
                System.out.println("Timer registered");
                payload = new JsonObject();
                payload.addProperty("msgType", input.get("method").getAsString());
                payload.addProperty("result", true);
                out.println(payload.toString());
            }

            // Wait for game to start
            while (this.game == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Update called");
        if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith("WaitingRoom")) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                System.out.println("Timer tick: " + tick);
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
        payload.addProperty("msgType","wrTimerTick");
        payload.addProperty("tick", tick);
        out.println(payload.toString());
    }

}
