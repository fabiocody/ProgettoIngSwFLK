package it.polimi.ingsw.server;

import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.WindowPattern;
import java.util.*;
import java.util.stream.Collectors;


// This class represents a player
public class Player {

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
    private boolean secondTurnToBeJumped;

    // Locks
    private final Object favorTokensLock = new Object();
    private final Object windowPatternLock = new Object();
    private final Object privateObjectiveCardLock = new Object();
    private final Object activeLock = new Object();

    public Player(String nickname) {
        this.nickname = nickname;
        this.id = UUID.randomUUID();
    }

    public String getNickname() {
        return this.nickname;
    }

    UUID getId() {
        return id;
    }

    public int getFavorTokens() {
        synchronized (favorTokensLock) {
            return this.favorTokens;
        }
    }

    public void setFavorTokens(int favorTokens) {
        synchronized (favorTokensLock) {
            this.favorTokens = favorTokens;
        }
    }

    public List<WindowPattern> getWindowPatternList() {
        synchronized (windowPatternLock) {
            if (this.windowPatternList == null)
                throw new IllegalStateException("Window Pattern not assigned yet");
            return new Vector<>(this.windowPatternList);
        }
    }

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

    public WindowPattern getWindowPattern() {
        synchronized (windowPatternLock) {
            return this.windowPatternList.get(0);
        }
    }

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
            } else {
                throw new IllegalStateException("Cannot choose another Window Pattern");
            }
        }
    }

    public ObjectiveCard getPrivateObjectiveCard() {
        synchronized (privateObjectiveCardLock) {
            if (this.privateObjectiveCard == null)
                throw new IllegalStateException("Private Objective Card not assigned yet");
            return this.privateObjectiveCard;
        }
    }

    // This method is designed to allow only one assignment of privateObjectiveCard
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

    public boolean isDiePlacedInThisTurn() {
        return diePlacedInThisTurn;
    }

    public void setDiePlacedInThisTurn(boolean diePlacedInThisTurn) {
        this.diePlacedInThisTurn = diePlacedInThisTurn;
    }

    public boolean isToolCardUsedInThisTurn() {
        return toolCardUsedInThisTurn;
    }

    public void setToolCardUsedInThisTurn(boolean toolCardUsedInThisTurn) {
        this.toolCardUsedInThisTurn = toolCardUsedInThisTurn;
    }

    public boolean isSecondTurnToBeJumped() {
        return secondTurnToBeJumped;
    }

    public void setSecondTurnToBeJumped(boolean secondTurnToBeJumped) {
        this.secondTurnToBeJumped = secondTurnToBeJumped;
    }

    public boolean isActive() {
        synchronized (activeLock) {
            return active;
        }
    }

    public void setActive(boolean active) {
        synchronized (activeLock) {
            this.active = active;
        }
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public synchronized String toString() {
        return this.getNickname();
    }

}
