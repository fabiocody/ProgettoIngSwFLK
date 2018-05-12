package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;
import java.util.*;


public class ToolCard4 extends ToolCard {

    private boolean firstMoveDone;
    private Integer firstMoveIndex;

    public ToolCard4(Game game) {
        super("Lathekin", "Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento", game);
        this.firstMoveDone = false;
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
    public void effect(JsonObject data) throws InvalidEffectResultException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        if (!this.firstMoveDone) {
            firstMove(data, player);
            this.firstMoveDone = true;
        } else {
            secondMove(data, player);
            this.firstMoveDone = false;
            this.setUsed();
        }
    }

    private void firstMove(JsonObject data, Player player) throws InvalidEffectResultException {
        int fromIndex = this.linearizeIndex(data.get("fromCellX").getAsInt(), data.get("fromCellY").getAsInt());
        int toIndex = this.linearizeIndex(data.get("toCellX").getAsInt(), data.get("toCellY").getAsInt());
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, PlacementConstraint.standardConstraint());
            this.firstMoveIndex = toIndex;
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException("Invalid Placement");
        }
    }

    private void secondMove(JsonObject data, Player player) throws InvalidEffectResultException {
        int fromIndex = this.linearizeIndex(data.get("fromCellX").getAsInt(), data.get("fromCellY").getAsInt());
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        int toIndex = this.linearizeIndex(data.get("toCellX").getAsInt(), data.get("toCellY").getAsInt());
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, PlacementConstraint.standardConstraint());
            this.firstMoveIndex = null;
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException("Invalid Placement");
        }
    }

}
