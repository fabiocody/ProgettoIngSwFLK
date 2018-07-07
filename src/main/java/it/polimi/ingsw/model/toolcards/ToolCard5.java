package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_5_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.INVALID_MOVE;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard5 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard5(Game game) {
        super(TOOL_CARD_5_NAME, "Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"draftPoolIndex": &lt;intstring&gt;,<br>
     *         &ensp;"roundTrackIndex": &lt;int&gt;<br>
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
        int roundTrackIndex = data.get(JsonFields.ROUND_TRACK_INDEX).getAsInt();
        if (roundTrackIndex < 0 || roundTrackIndex >= this.getGame().getRoundTrack().getFlattenedDice().size())
            throw new InvalidEffectArgumentException("Invalid roundTrackIndex: " + roundTrackIndex);
        try {
            Die fromDraftPool = this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            Die fromRoundTrack = this.getGame().getRoundTrack().getDice().get(this.getGame().getRoundTrack().getCurrentRoundDiceIndex()).remove(roundTrackIndex);
            this.getGame().getDiceGenerator().getDraftPool().add(draftPoolIndex, fromRoundTrack);
            this.getGame().getRoundTrack().getDice().get(this.getGame().getRoundTrack().getCurrentRoundDiceIndex()).add(roundTrackIndex, fromDraftPool);
        } catch (Exception e) {
            throw new InvalidEffectResultException(INVALID_MOVE);
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
        data.addProperty(JsonFields.ROUND_TRACK_INDEX, Constants.INDEX_CONSTANT);
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
