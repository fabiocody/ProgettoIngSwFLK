package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;


public class ToolCard3 extends ToolCard {

    public ToolCard3(Game game) {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\nDevi rispettare tutte le altre restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": <nickname: string>,
     *      "fromCellX": <int>,
     *      "fromCellY": <int>,
     *      "toCellX": <int>,
     *      "toCellY": <int>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        int fromCellX = data.get("fromCellX").getAsInt();
        int fromCellY = data.get("fromCellY").getAsInt();
        int fromIndex = this.linearizeIndex(fromCellX, fromCellY);
        if (fromIndex < 0 || fromIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid fromIndex: " + fromIndex + " (" + fromCellX + ", " + fromCellY + ")");
        int toCellX = data.get("toCellX").getAsInt();
        int toCellY = data.get("toCellY").getAsInt();
        int toIndex = this.linearizeIndex(toCellX, toCellY);
        if (toIndex < 0 || toIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid toIndex: " + toIndex + " (" + toCellX + ", " + toCellY + ")");
        PlacementConstraint constraint = new PositionConstraint(new ColorConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        this.moveDie(player, fromIndex, toIndex, constraint);
        this.setUsed();
    }

}
