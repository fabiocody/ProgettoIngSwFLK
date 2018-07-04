package it.polimi.ingsw.model.game;

import it.polimi.ingsw.server.SagradaServer;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.stream.*;


/**
 * This class handles the turns and the related timers.
 *
 * @author Fabio Codiglioni
 */
public class TurnManager extends Observable {
    // Is observed by RoundTrack

    private List<Player> players;
    private List<Integer> playersOrder;
    private int index;
    private CountdownTimer timer;
    private int timeout = 30;

    /**
     * @param players the list of players taking part in the Game.
     */
    public TurnManager(List<Player> players) {
        this.players = players;
        this.timeout = SagradaServer.getInstance().getGameTimeout();
        Stream<Integer> forwardRange = IntStream.range(0, this.getNumberOfPlayers()).boxed();
        Stream<Integer> backRange = IntStream.range(0, this.getNumberOfPlayers()).boxed().sorted(Collections.reverseOrder());
        this.playersOrder = Stream.concat(forwardRange, backRange).collect(Collectors.toList());
        this.index = 0;
        this.timer = new CountdownTimer(NotificationMessages.TURN_MANAGER);
        this.setActivePlayer(this.getCurrentPlayer());
    }

    /**
     * @author Fabio Codiglioni
     * @return the number of players taking part in the Game.
     */
    private int getNumberOfPlayers() {
        return this.players.size();
    }

    /**
     * @author Fabio Codiglioni
     * @return the current index in the sequence of players.
     */
    private int getCurrentPlayerIndex() {
        return this.playersOrder.get(index);
    }

    /**
     * @author Fabio Codiglioni
     * @return the current Player.
     */
    Player getCurrentPlayer() {
        return this.players.get(this.getCurrentPlayerIndex());
    }

    public String getActivePlayer() {
        return getCurrentPlayer().getNickname();
    }

    public void subscribeToTimer(Observer observer) {
        this.timer.addObserver(observer);
        this.timer.schedule(() -> {
            this.getCurrentPlayer().setSuspended(true);
            this.nextTurn();
        }, this.timeout);
    }

    /**
     * This method set the specified Player as active, and all the other players as inactive.
     *
     * @author Fabio Codiglioni
     * @param player the player to be set active.
     */
    private void setActivePlayer(Player player) {
        for (Player p : this.players) {
            p.setDiePlacedInThisTurn(false);
            p.setToolCardUsedThisTurn(false);
            p.setActive(p.equals(player));
            if (p.isActive() && p.isSecondTurnToBeSkipped()) {
                p.setSecondTurnToBeSkipped(false);
                this.nextTurn();
            }
        }
    }

    void cancelTimer() {
        if (timer != null) timer.cancel();
    }

    /**
     * @return true if the round is in its second half.
     */
    public boolean isSecondHalfOfRound() {
        return this.index >= this.playersOrder.size() / 2;
    }

    public int countNotSuspendedPlayers() {
        return (int) players.stream()
                .filter(player -> !player.isSuspended())
                .count();
    }

    /**
     * This method has to be called by each Player when the end their turn.
     *
     * @author Fabio Codiglioni
     */
    public void nextTurn() {
        this.timer.cancel();
        if (countNotSuspendedPlayers() <= 1) {
            this.setChanged();
            this.notifyObservers(NotificationMessages.GAME_INTERRUPTED);
        } else {
            index++;
            if (this.index == this.playersOrder.size()) {
                this.index = 0;
                Collections.rotate(this.players, -1);   // shift starting player
                this.setChanged();
                this.notifyObservers(NotificationMessages.ROUND_INCREMENTED);
            }
            if (getCurrentPlayer().isSuspended())
                nextTurn();
            this.setActivePlayer(this.getCurrentPlayer());
        }
    }


}
