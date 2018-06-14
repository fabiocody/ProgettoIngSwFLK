package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.WaitingRoom;
import it.polimi.ingsw.util.*;
import joptsimple.*;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.*;


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
    private int wrTimeout = 30;
    private int gameTimeout = 60;

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

    public void start(int port, int wrTimeout, int gameTimeout, boolean debugActive) {
        this.port = port;
        this.wrTimeout = wrTimeout;
        this.gameTimeout = gameTimeout;
        Logger.setDebugActive(debugActive);
        WaitingRoom.getInstance().setTimeout(this.wrTimeout);
        new Thread(this::startSocketServer).start();
        this.startRMI();
    }

    /**
     *
     */
    private void startSocketServer() {
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

    private void startRMI() {
        try {
            LocateRegistry.createRegistry(this.port);
        } catch (RemoteException e) {

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
        return isNicknameUsedWR(nickname) || (isNicknameUsedGame(nickname) != null && !isNicknameSuspended(nickname));
        /*Stream<String> gameStream = this.getGames().stream()
                .flatMap(g -> g.getPlayers().stream())
                .map(Player::getNickname);
        Stream<String> waitingRoomStream = WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname);
        return Stream.concat(gameStream, waitingRoomStream)
                .anyMatch(n -> n.equals(nickname));*/
    }

    public synchronized boolean isNicknameUsedWR(String nickname) {
        return WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname)
                .anyMatch(n -> n.equals(nickname));
    }

    public synchronized Game isNicknameUsedGame(String nickname) {
        Optional<Game> game = this.getGames().stream()
                .filter(g -> g.isNicknameUsedInThisGame(nickname))
                .findFirst();
        return game.orElse(null);
    }

    public synchronized boolean isNicknameSuspended(String nickname) {
        return this.getGames().stream()
                .flatMap(g -> g.getPlayers().stream())
                .filter(player -> player.getNickname().equals(nickname))
                .anyMatch(Player::isSuspended);
    }

    public synchronized boolean isNicknameNotValid(String nickname){
        return nickname.contains(" ") || nickname.equals("") || nickname.length() > Constants.MAX_NICKNAME_LENGTH;
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
        if (o instanceof WaitingRoom)
            if (arg instanceof Game) {
                getGames().add((Game) arg);
            }
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(CLIArguments.DEBUG);
        parser.accepts(CLIArguments.WR_TIMEOUT).withRequiredArg().required().ofType(Integer.class);
        parser.accepts(CLIArguments.GAME_TIMEOUT).withRequiredArg().required().ofType(Integer.class);
        parser.accepts(CLIArguments.PORT).withRequiredArg().ofType(Integer.class);
        try {
            OptionSet options = parser.parse(args);
            int wrTimerout = (Integer) options.valueOf(CLIArguments.WR_TIMEOUT);
            int gameTimeout = (Integer) options.valueOf(CLIArguments.GAME_TIMEOUT);
            int port;
            if (options.has(CLIArguments.PORT)) {
                port = (int) options.valueOf(CLIArguments.PORT);
            } else {
                port = Constants.DEFAULT_PORT;
            }
            SagradaServer.getInstance().start(port, wrTimerout, gameTimeout, options.has(CLIArguments.DEBUG));
        } catch (OptionException e) {
            System.out.println("usage: sagradaserver [--debug] [--port PORT] --wr-timer Y --game-timeout Z");
        }
    }

}
