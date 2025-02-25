package it.polimi.ingsw.model.game;

import it.polimi.ingsw.server.SagradaServer;
import it.polimi.ingsw.shared.util.*;
import java.util.*;


/**
 * This class represent the waiting room of the game.
 *
 * @author Fabio Codiglioni
 */
public class WaitingRoom extends Observable {

    private static WaitingRoom instance;
    private List<Player> waitingPlayers;
    private int timeout = 60;
    private CountdownTimer timer;
    private boolean playerAdded = false;

    /**
     * This constructor is private since WaitingRoom is a singleton.
     *
     * @author Fabio Codiglioni
     */
    private WaitingRoom() {
        this.timer = new CountdownTimer(NotificationMessages.WAITING_ROOM);
    }

    /**
     * @author Fabio Codiglioni
     * @return the unique instance of WaitingRoom.
     */
    public static synchronized WaitingRoom getInstance() {
        if (instance == null)
            instance = new WaitingRoom();
        return instance;
    }

    /**
     * @author Fabio Codiglioni
     * @return the list of waiting players.
     */
    public synchronized List<Player> getWaitingPlayers() {
        if (this.waitingPlayers == null)
            this.waitingPlayers = new Vector<>();
        return this.waitingPlayers;
    }

    /**
     * @author Fabio Codiglioni
     * @param timeout the timer timeout.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @author Fabio Codiglioni
     * @return the instance of the Waiting Room Timer.
     */
    public CountdownTimer getTimer() {
        return this.timer;
    }

    /**
     * This method adds a player to the Waiting Room, and triggers Game creation when timer expires or when 4 players are reached.
     *
     * @author Fabio Codiglioni
     * @param nickname the nickname of the player who wants to login (must be unique game-wide).
     * @return a random UUID the players has to use to authenticate with the game.
     * @throws LoginFailedException thrown if the nickname is already present game-wide.
     */
    public synchronized UUID addPlayer(String nickname) throws LoginFailedException, NicknameAlreadyUsedInGameException {
        if (SagradaServer.getInstance().isNicknameNotValid(nickname) || SagradaServer.getInstance().isNicknameUsed(nickname))
            throw new LoginFailedException(nickname);
        Game belongingGame = SagradaServer.getInstance().isNicknameUsedGame(nickname);
        if (belongingGame != null) {
            throw new NicknameAlreadyUsedInGameException(nickname, belongingGame);
        } else {
            Player player = new Player(nickname);
            this.getWaitingPlayers().add(player);
            this.setChanged();
            this.notifyObservers(this.getWaitingPlayers());
            if (this.getWaitingPlayers().size() == 2) {
                this.timer.cancel();
                this.timer.schedule(this::createGame, this.timeout);
            } else if (this.getWaitingPlayers().size() == Constants.MAX_NUMBER_OF_PLAYERS) {
                new Thread(this::createGame).start();
            }
            playerAdded = true;
            notifyAll();
            return player.getId();
        }
    }

    /**
     * This method removes a Player from the Waiting Room.
     * It must not be called when transferring a Player from the Waiting Room to an actual Game.
     *
     * @author Fabio Codiglioni
     * @param nickname the nickname of the Player that wants to be removed.
     */
    public synchronized void removePlayer(String nickname) {
        Optional<Player> player = this.getWaitingPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();
        if (player.isPresent()) {
            this.getWaitingPlayers().remove(player.get());
            if (this.getWaitingPlayers().size() < 2)
                this.timer.cancel(true);
            this.setChanged();
            this.notifyObservers(this.getWaitingPlayers());
        }
    }

    /**
     * This method creates a new game with the first N players of the list.
     * This timer is canceled and SagradaServer is notified.
     *
     * @author Fabio Codiglioni
     */
    private synchronized void createGame() {
        while (!playerAdded) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.printStackTrace(e);
                Thread.currentThread().interrupt();
            }
        }
        playerAdded = false;
        this.timer.cancel();
        this.setChanged();
        this.notifyObservers(new Game(this.getWaitingPlayers()));
        this.getWaitingPlayers().clear();
    }

}
