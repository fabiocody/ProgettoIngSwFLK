package it.polimi.ingsw.objectivecards;

import java.io.Serializable;


public abstract class ObjectiveCard implements Serializable {

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

    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectiveCard))
            return false;
        else
            return this.name.equals(((ObjectiveCard) obj).getName());
    }
}
