package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Main server class
public class SagradaServer implements Observer {
    // Observes WaitingRoom

    private List<Game> games;

    //Server related
    private int port;
    private ServerSocket serverSocket;
    private boolean run = true;

    public SagradaServer(int port) {
        WaitingRoom.getInstance().addObserver(this);
        this.port = port;
        this.startSocketServer();
    }

    private void startSocketServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Server Online");
        while (run) {
            try {
                Socket socket = this.serverSocket.accept();
                executor.submit(new ServerSocketHandler(socket));
            } catch (IOException e) {
                e.printStackTrace();
                run = false;
            }
        }
        executor.shutdown();
    }

    List<Game> getGames() {
        if (this.games == null)
            this.games = new Vector<>();
        return this.games;
    }

    // Observer method, instantiate and start a game when called
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom && arg instanceof Game)
            getGames().add((Game) arg);
    }

}
