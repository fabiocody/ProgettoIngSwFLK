package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * This class represents a player logged in the game.
 *
 * @author Fabio Codiglioni
 */
public class Player extends Observable {

    // Attributes
    private String nickname;
    private UUID id;
    private int favorTokens;
    private List<WindowPattern> windowPatternList;
    private ObjectiveCard privateObjectiveCard;
    private boolean active;
    private boolean suspended;

    // Flags
    private boolean windowPatternListSet;   // True iff this player has already been assigned a Window Pattern (to prevent alterations)
    private boolean privateObjectiveCardSet;    // True iff this player has already been assigned a Private Objective Card (to prevent alterations)
    private boolean windowPatternChosen;
    private boolean diePlacedInThisTurn;
    private boolean toolCardUsedThisTurn;
    private boolean secondTurnToBeSkipped;

    /**
     * This method creates a Player object with a given nickname and computes a random UUID of it.
     *
     * @author Fabio Codiglioni
     * @param nickname the nickname of the player.
     */
    public Player(String nickname) {
        this.nickname = nickname;
        this.id = UUID.randomUUID();
    }

    /**
     * @author Fabio Codiglioni
     * @return the nickname of the Player.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * @author Team
     * @return the UUID of the Player.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @author Fabio Codiglioni
     * @return the remaining Favor Tokens of the Player.
     */
    public int getFavorTokens() {
        return this.favorTokens;
    }

    /**
     * @author Fabio Codiglioni
     * @param favorTokens the new value of the Favor Tokens.
     */
    public void setFavorTokens(int favorTokens) {
        this.favorTokens = favorTokens;
    }

    /**
     * @author Team
     * @return the list of the Window Patterns amongst which the Player has to choose.
     */
    public List<WindowPattern> getWindowPatternList() {
        if (this.windowPatternList == null)
            throw new IllegalStateException("Window Pattern not assigned yet");
        return new Vector<>(this.windowPatternList);
    }

    /**
     * @author Team
     * @param windowPatterns the list of the Window Patterns amongst which the Player has to choose.
     */
    public void setWindowPatternList(List<WindowPattern> windowPatterns) {
        if (!windowPatternListSet) {
            this.windowPatternList = new Vector<>(windowPatterns);
            this.windowPatternListSet = true;
        } else {
            throw new IllegalStateException("Cannot set another list of Window Patterns");
        }
    }

    /**
     * @author Team
     * @return the chosen Window Pattern
     */
    public WindowPattern getWindowPattern() {
        return this.windowPatternList.get(0);
    }

    /**
     * This method is used to choose the Window Pattern the Player is going to use for the Game.
     *
     * @author Team
     * @param index the index of the list from which the player has to choose.
     */
    public void chooseWindowPattern(int index) {
        if (!windowPatternChosen) {
            WindowPattern chosen = this.windowPatternList.get(index);
            this.windowPatternList = this.windowPatternList.stream()
                    .filter(p -> p == chosen)
                    .collect(Collectors.toList());
            this.windowPatternChosen = true;
            this.setFavorTokens(this.getWindowPattern().getDifficulty());
            setChanged();
            notifyObservers();
        } else {
            throw new IllegalStateException("Cannot choose another Window Pattern");
        }
    }

    boolean isWindowPatternChosen() {
        return windowPatternChosen;
    }

    //throws InvalidPlacementException, DieAlreadyPlacedException
    public void placeDie(Die d, int x, int y) {
        if (this.isDiePlacedInThisTurn()) throw new DieAlreadyPlacedException(InterfaceMessages.DIE_ALREADY_PLACED_IN_THIS_TURN);
        this.getWindowPattern().placeDie(d, Constants.NUMBER_OF_PATTERN_COLUMNS * y + x);
        setDiePlacedInThisTurn(true);
    }

    public void placeDie(Die d, int position, PlacementConstraint constraint) {
        if (this.isDiePlacedInThisTurn()) throw new DieAlreadyPlacedException("you already placed a die this turn");
        this.getWindowPattern().placeDie(d, position, constraint);
        setDiePlacedInThisTurn(true);
    }

    /**
     * @author Fabio Codiglioni
     * @return the Private Objective Card of the Player.
     */
    public ObjectiveCard getPrivateObjectiveCard() {
        if (this.privateObjectiveCard == null)
            throw new IllegalStateException("Private Objective Card not assigned yet");
        return this.privateObjectiveCard;
    }

    /**
     * This method is designed to allow only one assignment of a Private Objective Card.
     *
     * @author Fabio Codiglioni
     * @param card the Private Objective Card of the Player.
     * @throws IllegalStateException thrown when calling this method more than once.
     */
    void setPrivateObjectiveCard(ObjectiveCard card) {
        if (!privateObjectiveCardSet) {
            this.privateObjectiveCard = card;
            this.privateObjectiveCardSet = true;
        } else {
            throw new IllegalStateException("Cannot set another Private Objective Card");
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the Player has already placed a Die in the current turn.
     */
    public boolean isDiePlacedInThisTurn() {
        return diePlacedInThisTurn;
    }

    /**
     * @author Fabio Codiglioni
     * @param value whether or not the Player has placed a Die in the current turn.
     */
    void setDiePlacedInThisTurn(boolean value) {
        this.diePlacedInThisTurn = value;
    }

    /**
     * @author Kai de Gast
     * @return true if the Player has already used a too card in the current turn.
     */
    public boolean isToolCardUsedThisTurn() {return toolCardUsedThisTurn; }

    /**
     * @author Kai de Gast
     * @param value whether or not the Player has used a tool card in the current turn.
     */
    public void setToolCardUsedThisTurn(boolean value) { this.toolCardUsedThisTurn= value; }

    /**
     * @author Fabio Codiglioni
     * @return true if the Player has to skip the second turn, as a result of a Tool Card effect.
     */
    public boolean isSecondTurnToBeSkipped() {
        return secondTurnToBeSkipped;
    }

    /**
     * @author Fabio Codiglioni
     * @param value whether or not the Player has to skip the second turn.
     */
    public void setSecondTurnToBeSkipped(boolean value) {
        this.secondTurnToBeSkipped = value;
    }

    /**
     * @author Fabio Codiglioni
     * @return true if this Player is the active Player, i.e. the one actually playing.
     */
    boolean isActive() {
        return active;
    }

    /**
     * @author Fabio Codiglioni
     * @param active whether or not the Player is active.
     */
    void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the Player has been suspended due to an expired timer or a network error.
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * @author Fabio Codiglioni
     * @param suspended whether or not the Player has to be suspended.
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
        if (suspended) {
            new Thread(() -> {
                setChanged();
                notifyObservers(NotificationMessages.CANCEL_TOOL_CARD);
            }).start();
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the nickname of the Player
     */
    public String toString() {
        return this.getNickname();
    }

}
