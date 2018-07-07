package it.polimi.ingsw.model.game;

public class Scores {

    private String nickname;
    private int privateObjectiveCardScore = 0;
    private int favorTokensScore = 0;
    private int finalScore = 0;

    /**
     * @param nickname
     */
    Scores(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the nickname of the client
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return private objective card score
     */
    int getPrivateObjectiveCardScore() {
        return privateObjectiveCardScore;
    }

    /**
     * @param score private objective card score
     */
    void setPrivateObjectiveCardScore(int score) {
        privateObjectiveCardScore = score;
    }

    /**
     * @return remaining favor token score
     */
    int getFavorTokensScore() {
        return favorTokensScore;
    }

    /**
     * @param score remaining favor token score
     */
    void setFavorTokensScore(int score) {
        favorTokensScore = score;
    }

    /**
     * @return final score
     */
    public int getFinalScore() {
        return finalScore;
    }

    /**
     * @param score final score
     */
    void setFinalScore(int score) {
        finalScore = score;
    }

    @Override
    public String toString() {
        return getNickname() + "'s final score\nFinal score: " + getFinalScore() + "\nPrivate Objective Card Score: " + getPrivateObjectiveCardScore() + "\nFavor tokens: " + getFavorTokensScore() + "\n";
    }
}
