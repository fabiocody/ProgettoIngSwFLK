package it.polimi.ingsw.client;

import java.io.*;
import java.util.*;


public class ClientCLI extends Client {

    private BufferedReader stdin;
    private boolean active = false;

    ClientCLI(ClientNetwork network, boolean debugActive) {
        super(network, debugActive);
    }

    void start() {
        try {
            this.stdin = new BufferedReader(new InputStreamReader(System.in));
            do addPlayer(); while (!this.isLogged());
            Thread.sleep(100 * 1000);
            // TODO Wait until game starts
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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork) {
            if (arg instanceof Iterable) {
                log(arg.toString());
            } else if (arg instanceof Integer) {
                log(arg.toString());
            }
        }
    }

}
