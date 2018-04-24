package it.polimi.ingsw.objective_cards;


public abstract class ObjectiveCard {

    private String name;
    private String description;
    private Integer score;

    public ObjectiveCard(String name, String description, Integer score) {
        this.name = name;
        this.description = description;
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getScore() {
        return this.score;
    }

    public abstract int calcScore();

}
