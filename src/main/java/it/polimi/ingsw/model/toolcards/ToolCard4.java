package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.JsonFields;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard4 extends ToolCard {

    private boolean firstMoveDone;
    private Integer firstMoveIndex;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard4(Game game) {
        super("Lathekin", "Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento", game);
        this.firstMoveDone = false;
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"fromCellX": &lt;int&gt;,<br>
     *         &ensp;"fromCellY": &lt;int&gt;,<br>
     *         &ensp;"toCellX": &lt;int&gt;,<br>
     *         &ensp;"toCellY": &lt;int&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        int fromCellX = data.get("fromCellX").getAsInt();
        int fromCellY = data.get("fromCellY").getAsInt();
        int fromIndex = this.linearizeIndex(fromCellX, fromCellY);
        if (fromIndex < 0 || fromIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid fromIndex: " + fromIndex + " (" + fromCellX + ", " + fromCellY + ")");
        int toCellX = data.get("toCellX").getAsInt();
        int toCellY = data.get("toCellY").getAsInt();
        int toIndex = this.linearizeIndex(toCellX, toCellY);
        if (toIndex < 0 || toIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid toIndex: " + toIndex + " (" + toCellX + ", " + toCellY + ")");
        if (!this.firstMoveDone) {
            firstMove(player, fromIndex, toIndex);
            this.firstMoveDone = true;
        } else {
            secondMove(player, fromIndex, toIndex);
            this.firstMoveDone = false;
            this.setUsed();
            setChanged();
            notifyObservers("$useToolCard$");
        }
    }

    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, "requiredData");
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.PLAYER, "$nickname$");
        data.addProperty(JsonFields.FROM_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.FROM_CELL_Y, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This methods executes the first of the two movements.
     *
     * @param player the player using the card.
     * @param fromIndex the starting cell of the movement.
     * @param toIndex the end cell of the movement.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void firstMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = toIndex;
    }

    /**
     * This method executes the second of the two movements.
     *
     * @param player the player using the card.
     * @param fromIndex the stating cell of the movement.
     * @param toIndex the end cell of the movement.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void secondMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = null;
    }

}
