package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import java.util.List;

import static it.polimi.ingsw.shared.util.InterfaceMessages.DIE_INVALID_POSITION;


/**
 * This class is the base class to all the Tool Cards.
 *
 * @author Fabio Codiglioni
 */
public abstract class ToolCard {

    private String name;
    private String description;
    private boolean used;
    private Game game;

    /**
     * @author Fabio Codiglioni
     * @param name the name of the Tool Card.
     * @param description the description of the Tool Card.
     * @param game the Game object the card is part of.
     * @see Game
     */
    public ToolCard(String name, String description, Game game) {
        this.name = name;
        this.description = description;
        this.used = false;
        this.game = game;
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
     * @return a boolean value representing whether or not the card has already been used at least once.
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * This method is used to mark the card as used.
     *
     * @author Fabio Codiglioni
     */
    public void setUsed() {
        this.used = true;
    }

    /**
     * @author Fabio Codiglioni
     * @return the game object the card is part of.
     */
    Game getGame() {
        return game;
    }

    /**
     * This method represents the effect of the Tool Card.
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public abstract void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException;

    /**
     * This method is used to cancel the usage of a tool card by a player,
     * if empty the tool card doesn't need a cancel method
     *
     * @author Team
     * @param player the player
     */
    public abstract void cancel(Player player);

    /**
     * @return the required data for the Tool Card
     */
    public abstract List<String> getRequiredData();

    /**
     * This method is used to send a JsonObject containing the fields that the user will have to fill to use this tool card
     *
     * @return JsonObject containing the required fields filled with momentary constants
     */
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.REQUIRED_DATA.getString());
        JsonObject data = new JsonObject();
        getRequiredData().forEach(rd -> data.addProperty(rd, Constants.INDEX_CONSTANT));
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This method is useful since we implemented the grid of each Window Pattern as a linear array,
     * thus we needed a way to convert from xy coordinates (used elsewhere) to a linear index.
     *
     * @author Fabio Codiglioni
     * @param x the x coordinate of the cell.
     * @param y the y coordinate of the cell.
     * @return the linearized index of the cell
     */
    int linearizeIndex(int x, int y) {
        return y* Constants.NUMBER_OF_PATTERN_COLUMNS + x;
    }

    /**
     * @author Fabio Codiglioni
     * @param player the player whose grid must be used for the movement.
     * @param fromIndex the starting index of the movement.
     * @param toIndex the ending index of the movement.
     * @param constraint the placement constraint of the movement.
     * @throws InvalidEffectResultException thrown when the specified movement is not possible.
     * @see #effect(JsonObject)
     * @see WindowPattern#moveDie(int, int, PlacementConstraint)
     * @see PlacementConstraint
     */
    void moveDie(Player player, int fromIndex, int toIndex, PlacementConstraint constraint) throws InvalidEffectResultException {
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, constraint);
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException(DIE_INVALID_POSITION);
        }
    }

    /**
     * @param data the data required by the Tool Card
     * @param player the Player object representing the player using the ToolCard
     * @return the linearized "from" index
     * @throws InvalidEffectArgumentException when the index if out of bound
     */
    int getFromIndex(JsonObject data, Player player) throws InvalidEffectArgumentException {
        int fromCellX = data.get(JsonFields.FROM_CELL_X).getAsInt();
        int fromCellY = data.get(JsonFields.FROM_CELL_Y).getAsInt();
        int fromIndex = this.linearizeIndex(fromCellX, fromCellY);
        if (fromIndex < 0 || fromIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid fromIndex: " + fromIndex + " (" + fromCellX + ", " + fromCellY + ")");
        return fromIndex;
    }

    /**
     * @param data the data required by the Tool Card
     * @param player the Player object representing the player using the Tool Card
     * @return the linearized "to" index
     * @throws InvalidEffectArgumentException when the index is out of bound
     */
    int getToIndex(JsonObject data, Player player) throws InvalidEffectArgumentException {
        int toCellX = data.get(JsonFields.TO_CELL_X).getAsInt();
        int toCellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
        int toIndex = this.linearizeIndex(toCellX, toCellY);
        if (toIndex < 0 || toIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid toIndex: " + toIndex + " (" + toCellX + ", " + toCellY + ")");
        return toIndex;
    }

    /**
     * @author Fabio Codiglioni
     * @param obj the object to compare.
     * @return <code>true</code> if <code>obj</code> is an instance of <code>ToolCard</code> and the names are equals, <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ToolCard))
            return false;
        else
            return this.name.equals(((ToolCard) obj).getName());
    }

    /**
     * @author Fabio Codiglioni
     * @return the value returned from <code>super.hashCode</code>
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @author Fabio Codiglioni
     * @return a string in the form <code>"name -&gt; description</code>.
     */
    public String toString() {
        return this.getName() + " -> " + this.getDescription();
    }

}
