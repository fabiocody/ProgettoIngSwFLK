package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;


public class ToolCard9 extends ToolCard {

    public ToolCard9(Game game) {
        super("Riga in Sughero", "Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": <nickname: string>,
     *      "draftPoolIndex": <int>,
     *      "cellX": <int>,
     *      "cellY": <int>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check index
        Player player = this.getGame().getPlayerForNickname(data.get("player").getAsString());
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        int x = data.get("cellX").getAsInt();
        int y = data.get("cellY").getAsInt();
        PlacementConstraint constraint = new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(d, linearizeIndex(x, y), constraint);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            this.setUsed();
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException();
        }
    }

}
