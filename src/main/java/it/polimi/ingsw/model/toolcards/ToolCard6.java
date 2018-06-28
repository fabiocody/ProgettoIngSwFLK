package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_6_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard6 extends ToolCard {


    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard6(Game game) {
        super(TOOL_CARD_6_NAME, "Dopo aver scelto un dado, tira nuovamente quel dado\nSe non puoi piazzarlo, riponilo nella riserva", game);
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
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        rollDie(draftPoolIndex);
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
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
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
    }

}
