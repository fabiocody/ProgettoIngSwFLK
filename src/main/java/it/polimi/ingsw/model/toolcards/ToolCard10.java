package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.shared.util.JsonFields;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_10_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard10 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.DRAFT_POOL_INDEX);

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard10(Game game) {
        super(TOOL_CARD_10_NAME, "Dopo aver scelto un dado, giralo sulla faccia opposta\n6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    @Override
    public void effect(JsonObject data) throws InvalidEffectArgumentException {
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        int value = die.getValue();
        die.setValue(7 - value);
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

    @Override
    public List<String> getRequiredData() {
        return new ArrayList<>(requiredData);
    }

}
