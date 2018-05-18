package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


// Main server class
public class SagradaServer implements Observer {
    // Observes WaitingRoom

    private static SagradaServer instance;

    private int port;
    private boolean run = true;
    private List<Game> games;
    private boolean debugActive;

    private SagradaServer() {
        WaitingRoom.getInstance().addObserver(this);
        this.port = 42000;
    }

    public static SagradaServer getInstance() {
        if (instance == null)
            instance = new SagradaServer();
        return instance;
    }

    public void startSocketServer(boolean debugActive) {
        this.debugActive = debugActive;
        ExecutorService executor = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(this.port);){
            System.out.println("Server up and running");
            while (run) {
                Socket socket = serverSocket.accept();
                executor.submit(new ServerSocketHandler(socket));
            }
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSocketServer() {
        this.startSocketServer(false);
    }

    List<Game> getGames() {
        if (this.games == null)
            this.games = new Vector<>();
        return this.games;
    }

    public synchronized boolean isNicknameUsed(String nickname) {
        Stream<String> gameStream = this.getGames().stream()
                .flatMap(g -> g.getPlayers().stream())
                .map(Player::getNickname);
        Stream<String> waitingRoomStream = WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname);
        return Stream.concat(gameStream, waitingRoomStream)
                .anyMatch(n -> n.equals(nickname));
    }

    public boolean isDebugActive() {
        return debugActive;
    }

    // Observer method, instantiate and start a game when called
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom && arg instanceof Game)
            getGames().add((Game) arg);
    }

    public static void main(String[] args) {
        SagradaServer.getInstance().startSocketServer();
    }

}
