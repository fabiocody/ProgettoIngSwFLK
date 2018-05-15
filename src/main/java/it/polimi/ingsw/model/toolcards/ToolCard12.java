package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
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
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        if (this.firstMoveColor == null && this.firstMoveIndex == null) {
            this.firstMove(data);
        } else if (this.firstMoveColor != null && this.firstMoveIndex != null && !data.get("stop").getAsBoolean()) {
            this.secondMove(data);
            this.setUsed();
        } else if (data.get("stop").getAsBoolean()) {
            this.firstMoveIndex = null;
            this.firstMoveColor = null;
            this.setUsed();
        }
    }

    private void firstMove(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
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
        Colors dieColor = player.getWindowPattern().getCellAt(fromIndex).getPlacedDie().getColor();
        long numberOfDiceOfTheSameColorOnRoundTrack = this.getGame().getRoundTrack().getDice().stream()
                .map(Die::getColor)
                .filter(c -> c == dieColor)
                .count();
        if (numberOfDiceOfTheSameColorOnRoundTrack == 0)
            throw new InvalidEffectResultException("There are no dice of that color on the Round Track");
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveColor = player.getWindowPattern().getCellAt(toIndex).getPlacedDie().getColor();
        this.firstMoveIndex = toIndex;
    }

    private void secondMove(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
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
        if (player.getWindowPattern().getCellAt(fromIndex).getPlacedDie().getColor() != this.firstMoveColor)
            throw new InvalidEffectResultException("Colors don't match");
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = null;
        this.firstMoveColor = null;
    }

}
