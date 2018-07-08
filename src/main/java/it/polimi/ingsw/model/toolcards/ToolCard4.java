package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_4_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard4 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.FROM_CELL_X, JsonFields.FROM_CELL_Y, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y);

    private boolean firstMoveDone;
    private Integer firstMoveIndex;
    private Integer beforeFirstMoveIndex;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard4(Game game) {
        super(TOOL_CARD_4_NAME, "Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento", game);
        this.firstMoveDone = false;
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
     *         &ensp;"toCellY": &lt;int&gt;<br>
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
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int fromIndex = getFromIndex(data, player);
        int toIndex = getToIndex(data, player);
        if (fromIndex == toIndex)
            throw new InvalidEffectResultException(InterfaceMessages.SAME_POSITION_INDEX);
        if (!this.firstMoveDone) {
            firstMove(player, fromIndex, toIndex);
            this.firstMoveDone = true;
        } else {
            secondMove(player, fromIndex, toIndex);
            this.firstMoveIndex = null;
            this.beforeFirstMoveIndex = null;
            this.firstMoveDone = false;
        }
    }

    /**
     * This method executes the first of the two movements.
     *
     * @param player the player using the card.
     * @param fromIndex the starting cell of the movement.
     * @param toIndex the end cell of the movement.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void firstMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        this.beforeFirstMoveIndex = fromIndex;
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
        this.firstMoveIndex = toIndex;
    }

    /**
     * This method executes the second of the two movements.
     *
     * @param player the player using the card.
     * @param fromIndex the stating cell of the movement.
     * @param toIndex the end cell of the movement.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void secondMove(Player player, int fromIndex, int toIndex) throws InvalidEffectResultException {
        if (fromIndex == this.firstMoveIndex) throw new InvalidEffectResultException(InterfaceMessages.MULTIPLE_DIE_MOVEMENTS);
        this.moveDie(player, fromIndex, toIndex, PlacementConstraint.standardConstraint());
    }

    /**
     * This method is used to cancel the usage of a tool card by a player,
     * if empty the tool card doesn't need a cancel method
     *
     * @author Team
     * @param player the player
     */
    @Override
    public void cancel(Player player) {
        if (firstMoveDone)
            player.getWindowPattern().moveDie(firstMoveIndex,beforeFirstMoveIndex,new EmptyConstraint());
        this.firstMoveDone = false;
        this.firstMoveIndex = null;
        this.beforeFirstMoveIndex = null;
    }

    @Override
    public List<String> getRequiredData() {
        List<String> data = new ArrayList<>(requiredData);
        if (!firstMoveDone)
            data.add(JsonFields.CONTINUE);
        return data;
    }
}
