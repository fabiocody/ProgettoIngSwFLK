package it.polimi.ingsw.server;

import joptsimple.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


/**
 * Main server class
 *
 * @author Team
 */
public class SagradaServer implements Observer {
    // Observes WaitingRoom

    private static SagradaServer instance;

    private int port;
    private boolean run = true;
    private List<Game> games;
    private boolean debugActive;
    private int gameTimeout = 30;

    /**
     * this method is the constructor that sets the port and adds an observer to the waiting room
     */
    private SagradaServer() {
        WaitingRoom.getInstance().addObserver(this);
    }

    /**
     * @return the only instance of the server
     */
    public static synchronized SagradaServer getInstance() {
        if (instance == null)
            instance = new SagradaServer();
        return instance;
    }

    /**
     * @param wrTimeout the duration of the timer for the waiting room
     * @param gameTimeout the duration of the timer for the game
     * @param debugActive if present activates the debug messages
     */
    public void startSocketServer(int port, int wrTimeout, int gameTimeout, boolean debugActive) {
        this.port = port;
        this.debugActive = debugActive;
        this.gameTimeout = gameTimeout;
        WaitingRoom.getInstance().setTimeout(wrTimeout);
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

    /**
     * @return a list of all the active games
     */
    List<Game> getGames() {
        if (this.games == null)
            this.games = new Vector<>();
        return this.games;
    }

    /**
     * this method checks if a nickname is already used in an active game
     *
     * @param nickname the nickname that we want to check
     * @return a boolean true if the nickname is already in use or false otherwise
     */
    public synchronized boolean isNicknameUsed(String nickname) {
        Stream<String> gameStream = this.getGames().stream()
                .flatMap(g -> g.getPlayers().stream())
                .map(Player::getNickname);
        Stream<String> waitingRoomStream = WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname);
        return Stream.concat(gameStream, waitingRoomStream)
                .anyMatch(n -> n.equals(nickname));
    }

    /**
     * @return true if the debug option is active or false otherwise
     */
    public boolean isDebugActive() {
        return debugActive;
    }

    /**
     * @return the duration of the timer for the game
     */
    public int getGameTimeout() {
        return gameTimeout;
    }

    /**
     * Observer method, instantiate and start a game when called
     *
     * @param o the object that has triggered the update
     * @param arg the arguments of the update
     */
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom && arg instanceof Game)
            getGames().add((Game) arg);
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("debug");
        parser.accepts("wr-timeout").withRequiredArg().required().ofType(Integer.class);
        parser.accepts("game-timeout").withRequiredArg().required().ofType(Integer.class);
        parser.accepts("port").withRequiredArg().ofType(Integer.class);
        try {
            OptionSet options = parser.parse(args);
            int wrTimerout = (Integer) options.valueOf("wr-timeout");
            int gameTimeout = (Integer) options.valueOf("game-timeout");
            int port;
            if (options.has("port")) {
                port = (int) options.valueOf("port");
            } else {
                port = 42000;
            }
            SagradaServer.getInstance().startSocketServer(port, wrTimerout, gameTimeout, options.has("debug"));
        } catch (OptionException e) {
            System.out.println("usage: sagradaserver [--debug] [--port PORT] --wr-timer Y --game-timeout Z");
        }
    }

}
