package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.DieAlreadyPlacedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard11 extends ToolCard {

    private int state = 0;
    private int draftPoolIndex = Constants.INDEX_CONSTANT;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard11(Game game) {
        super("Diluente per Pasta Salda", "Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto\nScegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
     *         &ensp;"newValue": &lt;int&gt;,<br>
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
        if (state == 0) {
            draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
            if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
                throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
            this.exchangeDie(draftPoolIndex);

        } else if (state == 1) {
            int newValue = data.get(JsonFields.NEW_VALUE).getAsInt();
            if (newValue < 1 || newValue > 6)
                throw new InvalidEffectArgumentException("Invalid newValue: " + newValue);
            this.chooseValue(draftPoolIndex, newValue);
            String nickname = data.get(JsonFields.PLAYER).getAsString();
            Player player = this.getGame().getPlayer(nickname);
            int cellX = data.get(JsonFields.TO_CELL_X).getAsInt();
            int cellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
            int cellIndex = this.linearizeIndex(cellX, cellY);
            if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
                throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
            this.placeDie(player, draftPoolIndex, cellX, cellY, cellIndex);

        }
    }

    /**
     * This method is used to send a JsonObject containing the fields that the user will have to fill to use this tool card
     *
     * @author Kai de Gast
     * @return JsonObject containing the required fields filled with momentary constants
     */
    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.REQUIRED_DATA.getString());
        JsonObject data = new JsonObject();
        if(state==0){
            data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
            data.addProperty(JsonFields.CONTINUE, Constants.INDEX_CONSTANT);
        }
        else{
            data.addProperty(JsonFields.NEW_VALUE, Constants.INDEX_CONSTANT);
            data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
            data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        }
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This method handles the die exchanging part of this Tool Card effect.
     *
     * @param draftPoolIndex the index of the die from the Draft Pool.
     */
    private void exchangeDie(int draftPoolIndex) {
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        this.getGame().getDiceGenerator().putAway(d);
        this.getGame().getDiceGenerator().getDraftPool().set(draftPoolIndex, this.getGame().getDiceGenerator().draw());
        state = 1;
    }

    /**
     * This method handles the value choosing part of this Tool Card effect.
     *
     * @param draftPoolIndex the index of the die from the Draft Pool.
     * @param newValue the new value to be assigned to the die.
     */
    private void chooseValue(int draftPoolIndex, int newValue) {
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).setValue(newValue);
    }

    /**
     * This method handles the die placement part of this Tool Card effect.
     *
     * @param player the player using this Tool Card.
     * @param draftPoolIndex the index of the die from the Draft Pool.
     * @param cellIndex the index of the cell you want to move the die to.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void placeDie(Player player, int draftPoolIndex, int cellX, int cellY, int cellIndex) throws InvalidEffectResultException {
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.placeDie(die, cellX, cellY);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex); //TODO placeDie method removes die from draftpool in gameEndPoint
            this.state = 0;
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            Cell cell = player.getWindowPattern().getCellAt(cellIndex);
            throw new InvalidEffectResultException("Invalid placement on cell at index " + cellIndex + " (" + cell + ") of die " + die);
        }
    }

}