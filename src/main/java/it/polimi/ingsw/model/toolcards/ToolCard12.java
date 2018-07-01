package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.shared.util.Colors;
import it.polimi.ingsw.shared.util.*;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_12_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.NO_PROPER_COLOR_DIE_ON_ROUND_TRACK;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard12 extends ToolCard {

    private Colors firstMoveColor;
    private Integer firstMoveIndex;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard12(Game game) {
        super(TOOL_CARD_12_NAME, "Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"fromCellX": &lt;int&gt;,<br>
     *         &ensp;"fromCellY": &lt;int&gt;,<br>
     *         &ensp;"toCellX": &lt;int&gt;,<br>
     *         &ensp;"toCellY": &lt;int&gt;,<br>
     *         &ensp;"stop": &lt;bool&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        if (this.firstMoveColor == null && this.firstMoveIndex == null) {
            this.firstMove(data);
        } else if (this.firstMoveColor != null && this.firstMoveIndex != null && !data.get(JsonFields.STOP).getAsBoolean()) {
            this.secondMove(data);
        } else if (data.get(JsonFields.STOP).getAsBoolean()) {
            this.firstMoveIndex = null;
            this.firstMoveColor = null;
        }
    }

    /**
     * this method is used to send a JsonObject containing the fields that the user will have to fill to use this tool card
     *
     * @author Kai de Gast
     * @return JsonObject containing the required fields filled with momentary constants
     */
    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.REQUIRED_DATA.getString());
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.FROM_CELL_X, Constants.INDEX_CONSTANT); //for each movement
        data.addProperty(JsonFields.FROM_CELL_Y, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        if (this.firstMoveColor == null && this.firstMoveIndex == null) {
            data.addProperty(JsonFields.CONTINUE, Constants.INDEX_CONSTANT);
            data.addProperty(JsonFields.STOP, Constants.INDEX_CONSTANT);
        } else data.addProperty(JsonFields.STOP, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    /**
     * This method handles the first of the two movements.
     *
     * @param data the JSON object passed to <code>effect</code>.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     * @throws InvalidEffectArgumentException thrown when <code>data</code> contains a field with an invalid value.
     */
    private void firstMove(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int fromCellX = data.get(JsonFields.FROM_CELL_X).getAsInt();
        int fromCellY = data.get(JsonFields.FROM_CELL_Y).getAsInt();
        int fromIndex = this.linearizeIndex(fromCellX, fromCellY);
        if (fromIndex < 0 || fromIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid fromIndex: " + fromIndex + " (" + fromCellX + ", " + fromCellY + ")");
        int toCellX = data.get(JsonFields.TO_CELL_X).getAsInt();
        int toCellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
        int toIndex = this.linearizeIndex(toCellX, toCellY);
        if (toIndex < 0 || toIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid toIndex: " + toIndex + " (" + toCellX + ", " + toCellY + ")");
        Colors dieColor = player.getWindowPattern().getCell(fromIndex).getPlacedDie().getColor();
        long numberOfDiceOfTheSameColorOnRoundTrack = this.getGame().getRoundTrack().getFlattenedDice().stream()
                .map(Die::getColor)
                .filter(c -> c == dieColor)
                .count();
        if (numberOfDiceOfTheSameColorOnRoundTrack == 0)
            throw new InvalidEffectResultException(NO_PROPER_COLOR_DIE_ON_ROUND_TRACK);
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveColor = player.getWindowPattern().getCell(toIndex).getPlacedDie().getColor();
        this.firstMoveIndex = toIndex;
    }

    /**
     * This method handles the second of the two movements.
     *
     * @param data the JSON object passed to <code>effect</code>.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     * @throws InvalidEffectArgumentException thrown when <code>data</code> contains a field with an invalid value.
     */
    private void secondMove(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int fromCellX = data.get(JsonFields.FROM_CELL_X).getAsInt();
        int fromCellY = data.get(JsonFields.FROM_CELL_Y).getAsInt();
        int fromIndex = this.linearizeIndex(fromCellX, fromCellY);
        if (fromIndex < 0 || fromIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid fromIndex: " + fromIndex + " (" + fromCellX + ", " + fromCellY + ")");
        int toCellX = data.get(JsonFields.TO_CELL_X).getAsInt();
        int toCellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
        int toIndex = this.linearizeIndex(toCellX, toCellY);
        if (toIndex < 0 || toIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid toIndex: " + toIndex + " (" + toCellX + ", " + toCellY + ")");
        if (player.getWindowPattern().getCell(fromIndex).getPlacedDie().getColor() != this.firstMoveColor)
            throw new InvalidEffectResultException("Colors don't match");
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException("Cannot move the same die twice");
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = null;
        this.firstMoveColor = null;
    }

}
