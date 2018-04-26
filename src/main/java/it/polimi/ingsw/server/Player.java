package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.ObjectiveCard;


public class Player {

    private String nickname;
    private int favorTokens;
    private WindowPattern windowPattern;
    private boolean windowPatternSet;
    private ObjectiveCard privateObjectiveCard;
    private boolean privateObjectiveCardSet;

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

    public void setWindowPattern(WindowPattern windowPattern) {
        if (!windowPatternSet)
            this.windowPattern = windowPattern;
        else
            throw new IllegalStateException("Cannot set a new Window Pattern");
    }

    public ObjectiveCard getPrivateObjectiveCard() {
        return this.privateObjectiveCard;
    }

    public void setPrivateObjectiveCard(ObjectiveCard privateObjectiveCard) {
        if (!privateObjectiveCardSet)
            this.privateObjectiveCard = privateObjectiveCard;
        else
            throw new IllegalStateException("Cannot set a new Private Objective Card");
    }

}


class WindowPattern {   // TODO Remove mockup

}
