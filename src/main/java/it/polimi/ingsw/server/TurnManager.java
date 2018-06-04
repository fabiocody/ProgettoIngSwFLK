package it.polimi.ingsw.server;

import it.polimi.ingsw.util.CountdownTimer;
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
    private boolean roundOver;

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
        this.timer = new CountdownTimer("TurnManager", this.timeout);
        this.roundOver = false;
        this.setActivePlayer(this.getCurrentPlayer());
        // TODO Set timer for first turn of first round
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
    public Player getCurrentPlayer() {
        return this.players.get(this.getCurrentPlayerIndex());
    }

    /**
     * @author Fabio Codiglioni
     * @return the timer instance.
     */
    public CountdownTimer getTimer() {
        return this.timer;
    }

    /**
     * @author Luca dell'Oglio
     * @return if the round is over.
     */

    public boolean isRoundOver(){return this.roundOver;}

    /**
     * This method set the specified Player as active, and all the other players as inactive.
     *
     * @author Fabio Codiglioni
     * @param player the player to be set active.
     */
    private void setActivePlayer(Player player) {
        for (Player p : this.players) {
            p.setDiePlacedInThisTurn(false);
            p.setActive(p.equals(player));
            if (p.isActive() && p.isSecondTurnToBeSkipped())
                this.nextTurn();
        }
    }

    private void setSuspendedPlayer(String nickname, boolean suspended) {
        Optional<Player> player = this.players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();
        player.ifPresent(p -> p.setSuspended(suspended));
    }

    /**
     * @author Fabio Codiglioni
     * @param nickname the nickname of the Player that has to be suspended.
     */
    void suspendPlayer(String nickname) {
        this.setSuspendedPlayer(nickname, true);
    }

    /**
     * @author Fabio Codiglioni
     * @param nickname the nickname of the Player that has to be set as not suspended.
     */
    void unsuspendPlayer(String nickname) {
        this.setSuspendedPlayer(nickname, false);
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the round is still in its first half.
     */
    public boolean isFirstHalfOfRound() {
        return this.index < this.playersOrder.size() / 2;
    }

    /**
     * @return true if the round is in its second half.
     */
    public boolean isSecondHalfOfRound() {
        return this.index >= this.playersOrder.size() / 2;
    }

    /**
     * This method has to be called by each Player when the end their turn.
     *
     * @author Fabio Codiglioni
     */
    public void nextTurn() {
        this.timer.cancel();
        this.index++;
        this.roundOver = false;
        if (this.index == this.playersOrder.size()) {
            this.index = 0;
            Collections.rotate(this.players, -1);   // shift starting player
            this.roundOver = true;
            this.setChanged();
            this.notifyObservers();
        }
        this.setActivePlayer(this.getCurrentPlayer());
        //this.timer.schedule(this::nextTurn, this.timeout);        // TODO
    }


}
