package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.JsonFields;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard6 extends ToolCard {

    private boolean dieRolled;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard6(Game game) {
        super("Pennello per Pasta Salda", "Dopo aver scelto un dado, tira nuovamente quel dado\nSe non puoi piazzarlo, riponilo nella riserva", game);
        this.dieRolled = false;
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
     *         &ensp;"cellX": &lt;int&gt;,<br>
     *         &ensp;"cellY": &lt;int&gt;,<br>
     *         &ensp;"putAway": &lt;bool&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        //if (!dieRolled) {
        rollDie(draftPoolIndex);
        /*} else {
        placeDie(data, draftPoolIndex);
        }*/
        setChanged();
        notifyObservers("$useToolCard$");
    }

    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, "requiredData");
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
        /*data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.PUT_AWAY, Constants.INDEX_CONSTANT);*/
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This method handles the rolling of the specified die from the Draft Pool.
     *
     * @param draftPoolIndex the index of the die from the Draft Pool you want to roll.
     */
    private void rollDie(int draftPoolIndex) {
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).roll();
        dieRolled = true;
    }

    /**
     * This method handles the placement of the previously rolled die.
     *
     * @author Fabio Codiglioni
     * @param data the JSON data passed to the <code>effect</code> method.
     * @param draftPoolIndex the index of the die from the Draft Pool you want to roll.
     * @throws InvalidEffectResultException thrown when the placement if invalid.
     * @throws InvalidEffectArgumentException thrown when a field of data has an invalid value.
     */
    /*private void placeDie(JsonObject data, int draftPoolIndex) throws InvalidEffectResultException, InvalidEffectArgumentException {
        if (!data.get("putAway").getAsBoolean()) {
            String nickname = data.get("player").getAsString();
            Player player = this.getGame().getPlayerForNickname(nickname);
            int cellX = data.get("cellX").getAsInt();
            int cellY = data.get("cellY").getAsInt();
            int cellIndex = this.linearizeIndex(cellX, cellY);
            if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
                throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
            Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
            try {
                player.getWindowPattern().placeDie(die, cellIndex);
                this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
                dieRolled = false;
                this.setUsed();
            } catch (InvalidPlacementException e) {
                Cell cell = player.getWindowPattern().getCellAt(cellIndex);
                throw new InvalidEffectResultException("Invalid placement on cell at index " + cellIndex + " (" + cell + ") of die " + die);
            }
        } else {
            this.setUsed();
        }
    }*/

}
