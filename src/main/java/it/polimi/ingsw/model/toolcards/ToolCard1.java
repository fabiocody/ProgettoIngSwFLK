package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.InterfaceMessages;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_1_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard1 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard1(Game game) {
        super(TOOL_CARD_1_NAME, "Dopo aver scelto un dado, aumenta o diminuisci il valore del dado scelto di 1\nNon puoi cambiare un 6 in 1 o un 1 in 6", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *     &ensp;"draftPoolIndex": &lt;int&gt;, <br>
     *     &ensp;"delta": &lt;int&gt; <br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int delta = data.get(JsonFields.DELTA).getAsInt();
        if (delta != 1 && delta != -1)
            throw new InvalidEffectArgumentException("Invalid delta: " + delta);
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        int newValue = d.getValue() + delta;
        if(newValue == 7)
            throw new InvalidEffectResultException(InterfaceMessages.DIE_UPPER_BOUND);
        else if(newValue == 0)
            throw new InvalidEffectResultException(InterfaceMessages.DIE_LOWER_BOUND);
        else d.setValue(newValue);
    }

    /**
     * This method is used to send a JsonObject containing the fields that the user will have to fill to use this tool card
     *
     * @author Kai de Gast
     * @return JsonObject containing the required fields filled with momentary constants
     */
    public JsonObject requiredData(){
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.REQUIRED_DATA.getString());
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.DELTA, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This method is used to cancel the usage of a tool card by a player,
     * if empty the tool card doesn't need a cancel method
     *
     * @author Team
     * @param player the player
     */
    @Override
    public void cancel(Player player){
        // Nothing to cancel
    }

}
