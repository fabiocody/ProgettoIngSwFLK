package it.polimi.ingsw.model.objectivecards;

import it.polimi.ingsw.model.patterncards.Cell;


/**
 * This class is the base class to all the Objective Cards (both Public and Private)
 *
 * @author Fabio Codiglioni
 */
public abstract class ObjectiveCard {

    private String name;
    private String description;
    private Integer victoryPoints;

    /**
     * @author Fabio Codiglioni
     * @param name the name of the Objective Card.
     * @param description the description of the Objective Card.
     * @param victoryPoints the Victory Points awarded for fulfilling the requirements of the card.
     */
    public ObjectiveCard(String name, String description, Integer victoryPoints) {
        this.name = name;
        this.description = description;
        this.victoryPoints = victoryPoints;
    }

    /**
     * This constructor is a shorthand to ObjectiveCard(name, description, null),
     * used when a card has no fixed Victory Points.
     *
     * @author Fabio Codiglioni
     * @param name the name of the Objective Card.
     * @param description the description of Objective Card.
     * @see #ObjectiveCard(String, String, Integer)
     */
    public ObjectiveCard(String name, String description) {
        this(name, description, null);
    }

    /**
     * @author Fabio Codiglioni
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @author Fabio Codiglioni
     * @return the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @author Fabio Codiglioni
     * @return the value of Victory Points.
     */
    public Integer getVictoryPoints() {
        return this.victoryPoints;
    }

    /**
     * This method computes the Victory Points gained from the card
     *
     * @author Fabio Codiglioni
     * @param grid the grid of the player you want to compute Victory Points for.
     * @return the Victory Points gained from the card.
     */
    public abstract int calcScore(Cell[] grid);

    /**
     * @author Fabio Codiglioni
     * @param obj the object to compare.
     * @return <code>true</code> if <code>obj</code> is an instance of <code>ObjectiveCard</code> and the names are equal, <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectiveCard))
            return false;
        else
            return this.name.equals(((ObjectiveCard) obj).getName());
    }

    /**
     * @author Fabio Codiglioni
     * @return the value returned from <code>super.hashCode</code>.
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @author Fabio Codiglioni
     * @return a string in the form <code>"name -&gt; description"</code>.
     */
    public String toString() {
        return this.getName() + " -> " + this.getDescription();
    }

}
