package it.polimi.ingsw.model.game;

public class Scores {

    private String nickname;
    private int privateObjectiveCardScore = 0;
    private int favorTokensScore = 0;
    private int finalScore = 0;

    Scores(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    int getPrivateObjectiveCardScore() {
        return privateObjectiveCardScore;
    }

    void setPrivateObjectiveCardScore(int score) {
        privateObjectiveCardScore = score;
    }

    int getFavorTokensScore() {
        return favorTokensScore;
    }

    void setFavorTokensScore(int score) {
        favorTokensScore = score;
    }

    public int getFinalScore() {
        return finalScore;
    }

    void setFinalScore(int score) {
        finalScore = score;
    }

    @Override
    public String toString() {
        return getNickname() + "'s final score\nFinal score: " + getFinalScore() + "\nPrivate Objective Card Score: " + getPrivateObjectiveCardScore() + "\nFavor tokens: " + getFavorTokensScore() + "\n";
    }
}
