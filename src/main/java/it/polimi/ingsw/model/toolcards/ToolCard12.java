package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_12_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.NO_PROPER_COLOR_DIE_ON_ROUND_TRACK;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard12 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.FROM_CELL_X, JsonFields.FROM_CELL_Y, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y, JsonFields.STOP);

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
    @Override
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
     * This method handles the first of the two movements.
     *
     * @param data the JSON object passed to <code>effect</code>.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     * @throws InvalidEffectArgumentException thrown when <code>data</code> contains a field with an invalid value.
     */
    private void firstMove(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int fromIndex = getFromIndex(data, player);
        int toIndex = getToIndex(data, player);
        if (fromIndex == toIndex)
            throw new InvalidEffectArgumentException(InterfaceMessages.SAME_POSITION_INDEX);
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
        int fromIndex = getFromIndex(data, player);
        int toIndex = getToIndex(data, player);
        if (fromIndex == toIndex)
            throw new InvalidEffectArgumentException(InterfaceMessages.SAME_POSITION_INDEX);
        if (player.getWindowPattern().getCell(fromIndex).getPlacedDie().getColor() != this.firstMoveColor)
            throw new InvalidEffectResultException(InterfaceMessages.NOT_MATCHING_COLORS);
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException(InterfaceMessages.MULTIPLE_DIE_MOVEMENTS);
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = null;
        this.firstMoveColor = null;
    }

    /**
     * This method is used to cancel the usage of a tool card by a player,
     * if empty the tool card doesn't need a cancel method
     *
     * @author Team
     * @param player the player
     */
    @Override
    public void cancel(Player player){
        if(this.firstMoveIndex != null && this.firstMoveColor!= null) {
            if (!this.isUsed()) {
                player.setFavorTokens(player.getFavorTokens() - 1);
            } else {
                player.setFavorTokens(player.getFavorTokens() - 2);
            }
            this.setUsed();
            player.setToolCardUsedThisTurn(true);
        }
        this.firstMoveIndex = null;
        this.firstMoveColor = null;
    }

    @Override
    public List<String> getRequiredData() {
        List<String> data = new ArrayList<>(requiredData);
        if (firstMoveColor == null && firstMoveIndex == null)
            data.add(JsonFields.CONTINUE);
        return data;
    }

}
