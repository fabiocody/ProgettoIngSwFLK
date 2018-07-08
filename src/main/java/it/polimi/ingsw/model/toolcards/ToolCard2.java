package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_2_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard2 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.FROM_CELL_X, JsonFields.FROM_CELL_Y, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y);

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard2(Game game) {
        super(TOOL_CARD_2_NAME, "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore\nDevi rispettare tutte le altre restrizioni di piazzamento", game);
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
    public void effect(JsonObject data) throws InvalidEffectArgumentException, InvalidEffectResultException {
        PlacementConstraint constraint;
        String nickname = data.get(JsonFields.PLAYER).getAsString();
        Player player = this.getGame().getPlayer(nickname);
        int fromIndex = getFromIndex(data, player);
        int toIndex = getToIndex(data, player);
        if (fromIndex == toIndex)
            throw new InvalidEffectResultException(InterfaceMessages.SAME_POSITION_INDEX);
        constraint = (player.getWindowPattern().checkIfOnlyOneDie()) ?
                new BorderConstraint(new ValueConstraint(new EmptyConstraint())) : new PositionConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        this.moveDie(player, fromIndex, toIndex, constraint);
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
        // Nothing to cancel
    }

    @Override
    public List<String> getRequiredData() {
        return new ArrayList<>(requiredData);
    }

}
