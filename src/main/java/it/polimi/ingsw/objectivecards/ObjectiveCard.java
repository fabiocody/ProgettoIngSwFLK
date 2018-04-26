package it.polimi.ingsw.objectivecards;


public abstract class ObjectiveCard {

    private String name;
    private String description;
    private Integer victoryPoints;

    public ObjectiveCard(String name, String description, Integer victoryPoints) {
        this.name = name;
        this.description = description;
        this.victoryPoints = victoryPoints;
    }

    public ObjectiveCard(String name, String description) {
        this(name, description, null);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getVictoryPoints() {
        return this.victoryPoints;
    }

    public abstract int calcScore();

    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectiveCard))
            return false;
        else
            return this.name.equals(((ObjectiveCard) obj).getName());
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return this.getName() + " -> " + this.getDescription();
    }

}
