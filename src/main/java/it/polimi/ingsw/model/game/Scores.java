package it.polimi.ingsw.model.game;

public class Scores {

    private String nickname;
    private int privateObjectiveCardScore = 0;
    private int favorTokensScore = 0;
    private int finalScore = 0;

    public Scores(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPrivateObjectiveCardScore() {
        return privateObjectiveCardScore;
    }

    public void setPrivateObjectiveCardScore(int privateObjectiveCardScore) {
        this.privateObjectiveCardScore = privateObjectiveCardScore;
    }

    public int getFavorTokensScore() {
        return favorTokensScore;
    }

    public void setFavorTokensScore(int favorTokensScore) {
        this.favorTokensScore = favorTokensScore;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    public String toString() {
        return getNickname() + "'s final score\nFinal score: " + getFinalScore() + "\nPrivate Objective Card Score: " + getPrivateObjectiveCardScore() + "\nFavor tokens: " + getFavorTokensScore() + "\n";
    }
}
