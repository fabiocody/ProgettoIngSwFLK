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
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        // TODO Check index
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int cellX = data.get("cellX").getAsInt();
        int cellY = data.get("cellY").getAsInt();
        int cellIndex = this.linearizeIndex(cellX, cellY);
        if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
        PlacementConstraint constraint = new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(d, cellIndex, constraint);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            this.setUsed();
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException();
        }
    }

}
