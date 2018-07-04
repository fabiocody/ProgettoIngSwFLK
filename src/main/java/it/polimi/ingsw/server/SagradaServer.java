package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.shared.rmi.ServerAPI;
import it.polimi.ingsw.shared.util.*;
import joptsimple.*;
import java.io.IOException;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;


/**
 * Main game class
 *
 * @author Team
 */
public class SagradaServer extends Observable implements Observer {
    // Observes WaitingRoom

    private static SagradaServer instance;

    private int port;
    private List<GameController> gameControllers;
    private int gameTimeout = 60;

    private ServerAPI welcomeRMIServer;

    /**
     * this method is the constructor that sets the port and adds an observer to the waiting room
     */
    private SagradaServer() {
        WaitingRoom.getInstance().addObserver(this);
    }

    /**
     * @return the only instance of the game
     */
    public static synchronized SagradaServer getInstance() {
        if (instance == null)
            instance = new SagradaServer();
        return instance;
    }

    private void start(String host, int port, int wrTimeout, int gameTimeout, boolean debugActive) {
        this.port = port;
        this.gameTimeout = gameTimeout;
        Logger.setDebugActive(debugActive);
        WaitingRoom.getInstance().setTimeout(wrTimeout);
        new Thread(this::startSocketServer).start();
        this.startRMI(host);
    }

    /**
     *
     */
    private void startSocketServer() {
        boolean run = true;
        ExecutorService executor = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(this.port)){
            Logger.log("Socket server up and running");
            while (run) {
                Socket socket = serverSocket.accept();
                ServerSocketHandler serverSocketHandler = new ServerSocketHandler(socket);
                WaitingRoomController.getInstance().addServerNetwork(serverSocketHandler);
                this.addObserver(serverSocketHandler);
                executor.submit(serverSocketHandler);
            }
            executor.shutdown();
        } catch (IOException e) {
            Logger.printStackTrace(e);
        }
    }

    private void startRMI(String host) {
        try {
            if (host != null) System.setProperty("java.rmi.game.hostname", host);
            Registry registry = LocateRegistry.createRegistry(Constants.DEFAULT_RMI_PORT);
            welcomeRMIServer = (ServerAPI) UnicastRemoteObject.exportObject(new ServerRMIHandler(), 0);
            registry.rebind(Constants.SERVER_RMI_NAME, welcomeRMIServer);
            Logger.log("RMI server up and running");
        } catch (RemoteException e) {
            Logger.error("Connection error: " + e.getMessage());
            Logger.error("RMI server couldn't be started");
        }
    }

    /**
     * @return a list of all the active game controllers
     */
    List<GameController> getGameControllers() {
        if (this.gameControllers == null)
            this.gameControllers = new Vector<>();
        return this.gameControllers;
    }

    GameController getGameController(Game game) {
        Optional<GameController> controller = getGameControllers().stream()
                .filter(gc -> gc.getGame() == game)
                .findFirst();
        if (controller.isPresent())
            return controller.get();
        else throw new NoSuchElementException("Can't find GameController with specified Game");
    }

    /**
     * this method checks if a nickname is already used in an active game
     *
     * @param nickname the nickname that we want to check
     * @return a boolean true if the nickname is already in use or false otherwise
     */
    public synchronized boolean isNicknameUsed(String nickname) {
        return isNicknameUsedWR(nickname) || (isNicknameUsedGame(nickname) != null && !isNicknameSuspended(nickname));
    }

    private synchronized boolean isNicknameUsedWR(String nickname) {
        return WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname)
                .anyMatch(n -> n.equals(nickname));
    }

    public synchronized Game isNicknameUsedGame(String nickname) {
        Optional<Game> game = this.getGameControllers().stream()
                .map(GameController::getGame)
                .filter(g -> g.isNicknameUsedInThisGame(nickname))
                .findFirst();
        return game.orElse(null);
    }

    private synchronized boolean isNicknameSuspended(String nickname) {
        return this.getGameControllers().stream()
                .flatMap(controller -> controller.getGame().getPlayers().stream())
                .filter(player -> player.getNickname().equals(nickname))
                .anyMatch(Player::isSuspended);
    }

    public synchronized boolean isNicknameNotValid(String nickname) {
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
        if (o instanceof WaitingRoom && arg instanceof Game) {
            GameController gameController = new GameController((Game) arg);
            this.getGameControllers().add(gameController);
            this.setChanged();
            this.notifyObservers(gameController);       // Notify ServerNetwork
        }
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(CLIArguments.DEBUG);
        parser.accepts(CLIArguments.HOST).withRequiredArg();
        parser.accepts(CLIArguments.WR_TIMEOUT).withRequiredArg().ofType(Integer.class).defaultsTo(Constants.DEFAULT_WR_TIMEOUT);
        parser.accepts(CLIArguments.GAME_TIMEOUT).withRequiredArg().ofType(Integer.class).defaultsTo(Constants.DEFAULT_GAME_TIMEOUT);
        parser.accepts(CLIArguments.PORT).withRequiredArg().ofType(Integer.class);
        try {
            OptionSet options = parser.parse(args);
            String host = options.has(CLIArguments.HOST) ? options.valueOf(CLIArguments.HOST).toString() : null;
            boolean debug = options.has(CLIArguments.DEBUG);
            int wrTimeout = (Integer) options.valueOf(CLIArguments.WR_TIMEOUT);
            int gameTimeout = (Integer) options.valueOf(CLIArguments.GAME_TIMEOUT);
            int port;
            if (options.has(CLIArguments.PORT)) {
                port = (int) options.valueOf(CLIArguments.PORT);
            } else {
                port = Constants.DEFAULT_PORT;
            }
            SagradaServer.getInstance().start(host, port, wrTimeout, gameTimeout, debug);
        } catch (OptionException | NullPointerException e) {
            Logger.println(InterfaceMessages.SERVER_USAGE_STRING);
        }
    }

}
