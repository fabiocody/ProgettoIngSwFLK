package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.ObjectiveCard;


// This class represents a player
public class Player {

    private String nickname;
    private int favorTokens;
    private WindowPattern windowPattern;
    private boolean windowPatternSet;   // True iff this player has already been assigned a Window Pattern (to prevent alterations)
    private ObjectiveCard privateObjectiveCard;
    private boolean privateObjectiveCardSet;    // True iff this player has already been assigned a Private Objective Card (to prevent alterations)
    private boolean active;

    public Player(String nickname, int favorTokens) {
        this.nickname = nickname;
        this.favorTokens = favorTokens;
        this.windowPatternSet = false;
        this.privateObjectiveCardSet = false;
    }

    public Player(String nickname) {
        this(nickname, 0);
    }

    public String getNickname() {
        return this.nickname;
    }

    public int getFavorTokens() {
        return this.favorTokens;
    }

    public void setFavorTokens(int favorTokens) {
        this.favorTokens = favorTokens;
    }

    public WindowPattern getWindowPattern() {
        return this.windowPattern;
    }

    // This method is designed to allow only one assignment of windowPattern
    public void setWindowPattern(WindowPattern windowPattern) {
        if (!windowPatternSet) {
            this.windowPattern = windowPattern;
            this.windowPatternSet = true;
        } else {
            throw new IllegalStateException("Cannot set another Window Pattern");
        }
    }

    public ObjectiveCard getPrivateObjectiveCard() {
        return this.privateObjectiveCard;
    }

    // This method is designed to allow only one assignment of privateObjectiveCard
    public void setPrivateObjectiveCard(ObjectiveCard privateObjectiveCard) {
        if (!privateObjectiveCardSet) {
            this.privateObjectiveCard = privateObjectiveCard;
            this.privateObjectiveCardSet = true;
        } else {
            throw new IllegalStateException("Cannot set another Private Objective Card");
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return this.getNickname();
    }

}


class WindowPattern {   // TODO Remove mockup

    public int getFavorTokens() {
        return 0;
    }


}
