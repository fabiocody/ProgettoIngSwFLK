package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;


public class ToolCard3 extends ToolCard {

    public ToolCard3(Game game) {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\nDevi rispettare tutte le altre restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": string,
     *      "fromCellX": int,
     *      "fromCellY": int,
     *      "toCellX": int,
     *      "toCellY": int
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        int fromIndex = this.linearizeIndex(data.get("fromCellX").getAsInt(), data.get("fromCellY").getAsInt());
        int toIndex = this.linearizeIndex(data.get("toCellX").getAsInt(), data.get("toCellY").getAsInt());
        PlacementConstraint constraint = new PositionConstraint(new ColorConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, constraint);
            this.setUsed();
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException();
        }
    }

}
