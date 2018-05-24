package it.polimi.ingsw.client;

import java.io.*;
import java.util.*;

import static it.polimi.ingsw.util.Ansi.ansi;


public class ClientCLI extends Client {

    private BufferedReader stdin;
    private boolean active = false;

    private String wrTimeout;
    private String wrPlayers;

    ClientCLI(ClientNetwork network, boolean debugActive) {
        super(network, debugActive);
    }

    void start() {
        try {
            this.stdin = new BufferedReader(new InputStreamReader(System.in));
            do addPlayer(); while (!this.isLogged());
            Thread.sleep(100 * 1000);
            // TODO Wait until game starts
            while (!active) Thread.sleep(10);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                getNetwork().teardown();
            } catch (IOException e) {
                error("Exception raised while tearing down");
                e.printStackTrace();
            }
        }
    }

    /**
     * this method is used to print the stdin
     *
     * @param prompt the input string
     * @return the string that has been read
     * @throws IOException socket error
     */
    private String input(String prompt) throws IOException {
        System.out.print(prompt + " ");
        String line = stdin.readLine();
        return line;
    }

    /**
     * this method handles log in of a player
     *
     * @throws IOException socket error
     */
    private void addPlayer() throws IOException {
        this.setNickname(this.input("Nickname >>>"));
        setUUID(this.getNetwork().addPlayer(this.getNickname()));
        setLogged(this.getUUID() != null);
        if (isLogged()) log("Login successful");
        else log("Login failed");
    }

    private static String concatWindowPatterns(String[] pattern1, String[] pattern2) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pattern1.length; i++) {
            builder.append(pattern1[i]);
            for (int k = 0; k < 5; k++) builder.append(" ");
            builder.append(pattern2[i]);
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private static void printWindowPatterns(List<String> patterns) {
        String toPrint = concatWindowPatterns(patterns.get(0).split("\n"), patterns.get(1).split("\n"));
        if (patterns.size() == 3)
            toPrint += "\n\n" + patterns.get(2);
        else if (patterns.size() == 4)
            toPrint += "\n\n" + concatWindowPatterns(patterns.get(2).split("\n"), patterns.get(3).split("\n"));
        System.out.println(ansi().clear().a(toPrint));
    }

    private void updateWaitingRoomMessages() {
        System.out.println(ansi().clear()
                .a("Waiting players: ")
                .a(this.wrPlayers)
                .a("\n")
                .a("Time left to game start: ")
                .a(this.wrTimeout != null ? this.wrTimeout : "∞")
        );
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork) {
            if (arg instanceof List) {      // Window Patterns
                printWindowPatterns((List) arg);
            } else if (arg instanceof Integer) {    // Timer ticks
                this.wrTimeout = arg.toString();
                updateWaitingRoomMessages();
            } else if (arg instanceof Iterable) {   // Players
                this.wrPlayers = arg.toString().replace("[", "")
                        .replace("]", "")
                        .replace("\",", ",")
                        .replace("\"", " ");
                updateWaitingRoomMessages();
            }
        }
    }

}
