package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.server.Game;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.JsonFields;
import it.polimi.ingsw.util.Methods;
import it.polimi.ingsw.util.NotificationsMessages;


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
        super("Pinza Sgrossatrice", "Dopo aver scelto un dado, aumenta o diminuisci il valore del dado scelto di 1\nNon puoi cambiare un 6 in 1 o un 1 in 6", game);
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
        if (d.getValue() == 1 && newValue == 6 || d.getValue() == 6 && newValue == 1)
            throw new InvalidEffectResultException("Cannot make a 1 into 6 or a 6 into 1");
        else d.setValue(newValue);
        setChanged();
        notifyObservers(NotificationsMessages.USE_TOOL_CARD);
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

}
