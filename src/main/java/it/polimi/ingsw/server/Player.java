package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.ObjectiveCard;
import it.polimi.ingsw.patterncards.WindowPattern;

import java.util.UUID;


// This class represents a player
public class Player {

    // Attributes
    private String nickname;
    private UUID id;
    private int favorTokens;
    private WindowPattern windowPattern;
    private boolean windowPatternSet;   // True iff this player has already been assigned a Window Pattern (to prevent alterations)
    private ObjectiveCard privateObjectiveCard;
    private boolean privateObjectiveCardSet;    // True iff this player has already been assigned a Private Objective Card (to prevent alterations)
    private boolean active;

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

    public WindowPattern getWindowPattern() {
        synchronized (windowPatternLock) {
            if (this.windowPattern == null)
                throw new IllegalStateException("Window Pattern not assigned yet");
            return this.windowPattern;
        }
    }

    // This method is designed to allow only one assignment of windowPattern
    public void setWindowPattern(WindowPattern windowPattern) {
        synchronized (windowPatternLock) {
            if (!windowPatternSet) {
                this.windowPattern = windowPattern;
                this.windowPatternSet = true;
                synchronized (favorTokensLock) {
                    this.setFavorTokens(this.getWindowPattern().getDifficulty());
                }
            } else {
                throw new IllegalStateException("Cannot set another Window Pattern");
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

    public synchronized String toString() {
        return this.getNickname();
    }

}
