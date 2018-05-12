package it.polimi.ingsw.server;

import it.polimi.ingsw.util.CountdownTimer;
import java.io.*;
import java.net.Socket;
import java.util.*;


public class ServerSocketHandler implements Runnable, Observer {

    private Socket socket;

    public ServerSocketHandler(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            //TODO BODY
            System.out.println("Connection established!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof CountdownTimer) {
            // TODO "wr <tick>"
            // TODO "tm <tick>"
        }
    }

}
