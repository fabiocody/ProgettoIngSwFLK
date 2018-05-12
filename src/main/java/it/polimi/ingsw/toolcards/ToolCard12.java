package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.util.Colors;


public class ToolCard12 extends ToolCard {

    private Colors firstMoveColor;
    private Integer firstMoveIndex;


    public ToolCard12(Game game) {
        super("Taglierina Manuale", "Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": <nickname: string>,
     *      "fromCellX": <int>,
     *      "fromCellY": <int>,
     *      "toCellX": <int>,
     *      "toCellY": <int>,
     *      "stop": <bool>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        if (this.firstMoveColor == null && this.firstMoveIndex == null) {
            this.firstMove(data, player);
        } else if (this.firstMoveColor != null && this.firstMoveIndex != null && !data.get("stop").getAsBoolean()) {
            this.secondMove(data, player);
            this.setUsed();
        } else if (data.get("stop").getAsBoolean()) {
            this.firstMoveIndex = null;
            this.firstMoveColor = null;
            this.setUsed();
        }
    }

    private void firstMove(JsonObject data, Player player) throws InvalidEffectResultException {
        int fromIndex = this.linearizeIndex(data.get("fromCellX").getAsInt(), data.get("fromCellY").getAsInt());
        Colors dieColor = player.getWindowPattern().getCellAt(fromIndex).getPlacedDie().getColor();
        long numberOfDiceOfTheSameColorOnRoundTrack = this.getGame().getRoundTrack().getDice().stream()
                .map(Die::getColor)
                .filter(c -> c == dieColor)
                .count();
        if (numberOfDiceOfTheSameColorOnRoundTrack == 0)
            throw new InvalidEffectResultException("There are no dice of that color on the Round Track");
        int toIndex = this.linearizeIndex(data.get("toCellX").getAsInt(), data.get("toCellY").getAsInt());
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, PlacementConstraint.standardConstraint());
            this.firstMoveColor = player.getWindowPattern().getCellAt(toIndex).getPlacedDie().getColor();
            this.firstMoveIndex = toIndex;
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException("Invalid Placement");
        }
    }

    private void secondMove(JsonObject data, Player player) throws InvalidEffectResultException {
        int fromIndex = this.linearizeIndex(data.get("fromCellX").getAsInt(), data.get("fromCellY").getAsInt());
        if (player.getWindowPattern().getCellAt(fromIndex).getPlacedDie().getColor() != this.firstMoveColor)
            throw new InvalidEffectResultException("Colors don't match");
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        int toIndex = this.linearizeIndex(data.get("toCellX").getAsInt(), data.get("toCellY").getAsInt());
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, PlacementConstraint.standardConstraint());
            this.firstMoveIndex = null;
            this.firstMoveColor = null;
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException("Invalid Placement");
        }
    }

}
