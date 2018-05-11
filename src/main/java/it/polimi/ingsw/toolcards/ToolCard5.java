package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.server.Game;


public class ToolCard5 extends ToolCard {

    public ToolCard5(Game game) {
        super("Taglierina circolare", "Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round", game);
    }

    /*
     *  JSON Format
     *  {
     *      "draftPoolIndex": int,
     *      "roundTrackIndex": int
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        int roundTrackIndex = data.get("roundTrackIndex").getAsInt();
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
