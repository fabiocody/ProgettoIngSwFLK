package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.server.Game;


public class ToolCard5 extends ToolCard {

    public ToolCard5(Game game) {
        super("Taglierina circolare", "Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round", game);
    }

    /*
     *  JSON Format
     *  {
     *      "draftPoolIndex": <int>,
     *      "roundTrackIndex": <int>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        // TODO Check indexes
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int roundTrackIndex = data.get("roundTrackIndex").getAsInt();
        if (roundTrackIndex < 0 || roundTrackIndex >= this.getGame().getRoundTrack().getDice().size())
            throw new InvalidEffectArgumentException("Invalid roundTrackIndex: " + roundTrackIndex);
        try {
            Die fromDraftPool = this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            Die fromRoundTrack = this.getGame().getRoundTrack().getDice().remove(roundTrackIndex);
            this.getGame().getDiceGenerator().getDraftPool().add(draftPoolIndex, fromRoundTrack);
            this.getGame().getRoundTrack().getDice().add(roundTrackIndex, fromDraftPool);
        } catch (Exception e) {
            throw new InvalidEffectResultException();
        }
        this.setUsed();
    }

}
