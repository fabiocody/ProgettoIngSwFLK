package it.polimi.ingsw.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketHandler implements Runnable {

    private Socket socket;

    public ServerSocketHandler(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        Scanner in;
        PrintWriter out;

        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            //TODO BODY
            System.out.println("Connection established!!!");
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
