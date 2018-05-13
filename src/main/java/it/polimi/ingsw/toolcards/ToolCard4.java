package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.placementconstraints.*;
import it.polimi.ingsw.server.*;


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
        if (!this.firstMoveDone) {
            firstMove(player, fromIndex, toIndex);
            this.firstMoveDone = true;
        } else {
            secondMove(player, fromIndex, toIndex);
            this.firstMoveDone = false;
            this.setUsed();
        }
    }

    private void firstMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = toIndex;
    }

    private void secondMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = null;
    }

}
