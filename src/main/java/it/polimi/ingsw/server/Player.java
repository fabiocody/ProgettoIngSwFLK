package it.polimi.ingsw.server;

import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.NotificationsMessages;
import java.util.*;
import java.util.stream.Collectors;


/**
 * This class represents a player logged in the server.
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
    private boolean toolCardUsedInThisTurn;
    private boolean secondTurnToBeSkipped;

    // Locks
    private final Object favorTokensLock = new Object();
    private final Object windowPatternLock = new Object();
    private final Object privateObjectiveCardLock = new Object();
    private final Object activeLock = new Object();


    /**
     * This methods creates a Player object with a given nickname and computes a random UUID of it.
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
    UUID getId() {
        return id;
    }

    /**
     * @author Fabio Codiglioni
     * @return the remaining Favor Tokens of the Player.
     */
    public int getFavorTokens() {
        synchronized (favorTokensLock) {
            return this.favorTokens;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @param favorTokens the new value of the Favor Tokens.
     */
    public void setFavorTokens(int favorTokens) {
        synchronized (favorTokensLock) {
            this.favorTokens = favorTokens;
        }
    }

    /**
     * @author Team
     * @return the list of the Window Patterns amongst which the Player has to choose.
     */
    public List<WindowPattern> getWindowPatternList() {
        synchronized (windowPatternLock) {
            if (this.windowPatternList == null)
                throw new IllegalStateException("Window Pattern not assigned yet");
            return new Vector<>(this.windowPatternList);
        }
    }

    /**
     * @author Team
     * @param windowPatternList the list of the Window Patterns amongst which the Player has to choose.
     */
    public void setWindowPatternList(List<WindowPattern> windowPatternList) {
        synchronized (windowPatternLock) {
            if (!windowPatternListSet) {
                this.windowPatternList = windowPatternList;
                this.windowPatternListSet = true;
            } else {
                throw new IllegalStateException("Cannot set another list of Window Patterns");
            }
        }
    }

    /**
     * @author Team
     * @return the chosen Window Pattern
     */
    public WindowPattern getWindowPattern() {
        synchronized (windowPatternLock) {
            return this.windowPatternList.get(0);
        }
    }

    /**
     * This method is used to choose the Window Pattern the Player is going to use for the Game.
     *
     * @author Team
     * @param index the index of the list from which the player has to choose.
     */
    public void chooseWindowPattern(int index) {
        synchronized (windowPatternLock) {
            if (!windowPatternChosen) {
                WindowPattern chosen = this.windowPatternList.get(index);
                this.windowPatternList = this.windowPatternList.stream()
                        .filter(p -> p == chosen)
                        .collect(Collectors.toList());
                this.windowPatternChosen = true;
                synchronized (favorTokensLock) {
                    this.setFavorTokens(this.getWindowPattern().getDifficulty());
                }
                setChanged();
                notifyObservers();
            } else {
                throw new IllegalStateException("Cannot choose another Window Pattern");
            }
        }
    }

    public void placeDie(Die d, int x, int y) {
        if (this.isDiePlacedInThisTurn()) throw new DieAlreadyPlacedException("");
        this.getWindowPattern().placeDie(d, Constants.NUMBER_OF_PATTERN_COLUMNS * y + x);
        setDiePlacedInThisTurn(true);
        this.setChanged();
        this.notifyObservers(NotificationsMessages.PLACE_DIE);
    }


    public boolean isWindowPatternChosen() {
        return windowPatternChosen;
    }

    /**
     * @author Fabio Codiglioni
     * @return the Private Objective Card of the Player.
     */
    public ObjectiveCard getPrivateObjectiveCard() {
        synchronized (privateObjectiveCardLock) {
            if (this.privateObjectiveCard == null)
                throw new IllegalStateException("Private Objective Card not assigned yet");
            return this.privateObjectiveCard;
        }
    }

    /**
     * This method is designed to allow only one assignment of a Private Objective Card.
     *
     * @author Fabio Codiglioni
     * @param privateObjectiveCard the Private Objective Card of the Player.
     * @throws IllegalStateException thrown when calling this method more than once.
     */
    public void setPrivateObjectiveCard(ObjectiveCard privateObjectiveCard) {
        synchronized (privateObjectiveCardLock) {
            if (!privateObjectiveCardSet) {
                this.privateObjectiveCard = privateObjectiveCard;
                this.privateObjectiveCardSet = true;
            } else {
                throw new IllegalStateException("Cannot set another Private Objective Card");
            }
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
     * @param diePlacedInThisTurn whether or not the Player has placed a Die in the current turn.
     */
    public void setDiePlacedInThisTurn(boolean diePlacedInThisTurn) {
        this.diePlacedInThisTurn = diePlacedInThisTurn;
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the Player has already used a Tool Card in the current turn.
     */
    public boolean isToolCardUsedInThisTurn() {
        return toolCardUsedInThisTurn;
    }

    /**
     * @author Fabio Codiglioni
     * @param toolCardUsedInThisTurn whether or not the Player has already used a Tool Card in the current turn.
     */
    public void setToolCardUsedInThisTurn(boolean toolCardUsedInThisTurn) {
        this.toolCardUsedInThisTurn = toolCardUsedInThisTurn;
    }

    /**
     * @author Fabio Codiglioni
     * @return true if the Player has to skip the second turn, as a result of a Tool Card effect.
     */
    public boolean isSecondTurnToBeSkipped() {
        return secondTurnToBeSkipped;
    }

    /**
     * @author Fabio Codiglioni
     * @param secondTurnToBeSkipped whether or not the Player has to skip the second turn.
     */
    public void setSecondTurnToBeSkipped(boolean secondTurnToBeSkipped) {
        this.secondTurnToBeSkipped = secondTurnToBeSkipped;
    }

    /**
     * @author Fabio Codiglioni
     * @return true if this Player is the active Player, i.e. the one actually playing.
     */
    public boolean isActive() {
        synchronized (activeLock) {
            return active;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @param active whether or not the Player is active.
     */
    public void setActive(boolean active) {
        synchronized (activeLock) {
            this.active = active;
        }
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
            setChanged();
            notifyObservers(NotificationsMessages.SUSPENDED);
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the nickname of the Player
     */
    public synchronized String toString() {
        return this.getNickname();
    }

}
