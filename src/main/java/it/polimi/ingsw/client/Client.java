package it.polimi.ingsw.client;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.startClient();
    }

    public void startClient() {
        try {
            Socket socket = new Socket(ip, port);
            System.out.println("Connection established!!");
            socket.close();
        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 42000);
    }
}