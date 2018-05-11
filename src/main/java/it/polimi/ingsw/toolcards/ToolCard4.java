package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;
import java.util.*;


public class ToolCard4 extends ToolCard {

    public ToolCard4(Game game) {
        super("Lathekin", "Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": string,
     *      "1": {
     *          "fromCellX": int,
     *          "fromCellY": int,
     *          "toCellX": int,
     *          "toCellY": int
     *      },
     *      "2": {
     *          "fromCellX": int,
     *          "fromCellY": int,
     *          "toCellX": int,
     *          "toCellY": int
     *      }
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Dividere in due mosse
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        List<JsonObject> movements = Arrays.asList(data.get("1").getAsJsonObject(), data.get("2").getAsJsonObject());
        for (JsonObject movement : movements) {
            int fromIndex = this.linearizeIndex(movement.get("fromCellX").getAsInt(), movement.get("fromCellY").getAsInt());
            int toIndex = this.linearizeIndex(movement.get("toCellX").getAsInt(), movement.get("toCellY").getAsInt());
            try {
                player.getWindowPattern().moveDie(fromIndex, toIndex, PlacementConstraint.standardConstraint());
            } catch (InvalidPlacementException e) {
                throw new InvalidEffectResultException();
            }
        }
        this.setUsed();
    }

}
