package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.DieAlreadyPlacedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_9_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.DIE_INVALID_POSITION;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard9 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard9(Game game) {
        super(TOOL_CARD_9_NAME, "Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
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
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int cellX = data.get(JsonFields.TO_CELL_X).getAsInt();
        int cellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
        int cellIndex = this.linearizeIndex(cellX, cellY);
        if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
        PlacementConstraint constraint;
        if (player.getWindowPattern().isGridEmpty()) {
            constraint = new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
        } else {
            constraint = new ColorConstraint(new ValueConstraint(new EmptyConstraint()));
        }
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
                player.placeDie(d, cellIndex, constraint);
                this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            throw new InvalidEffectResultException(DIE_INVALID_POSITION);
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
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

}
