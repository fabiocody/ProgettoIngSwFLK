package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.server.Game;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.JsonFields;


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
        super("Taglierina circolare", "Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round", game);
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
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int roundTrackIndex = data.get("roundTrackIndex").getAsInt();
        if (roundTrackIndex < 0 || roundTrackIndex >= this.getGame().getRoundTrack().getAllDice().size())
            throw new InvalidEffectArgumentException("Invalid roundTrackIndex: " + roundTrackIndex);
        try {
            Die fromDraftPool = this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            Die fromRoundTrack = this.getGame().getRoundTrack().getRoundTrackDice()[this.getGame().getRoundTrack().getCurrentRound() - 1].remove(roundTrackIndex);
            this.getGame().getDiceGenerator().getDraftPool().add(draftPoolIndex, fromRoundTrack);
            this.getGame().getRoundTrack().getRoundTrackDice()[this.getGame().getRoundTrack().getCurrentRound() - 1].add(roundTrackIndex, fromDraftPool);
        } catch (Exception e) {
            throw new InvalidEffectResultException();
        }
        this.setUsed();
        setChanged();
        notifyObservers("$useToolCard$");
    }

    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, "requiredData");
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.ROUND_TRACK_INDEX, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

}
