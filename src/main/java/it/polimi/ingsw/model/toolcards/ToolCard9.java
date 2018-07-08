package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.DieAlreadyPlacedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_9_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.DIE_ALREADY_PLACED_IN_THIS_TURN;
import static it.polimi.ingsw.shared.util.InterfaceMessages.DIE_INVALID_POSITION;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard9 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.DRAFT_POOL_INDEX, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y);

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard9(Game game) {
        super(TOOL_CARD_9_NAME, "Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
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
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int cellIndex = getToIndex(data, player);
        PlacementConstraint constraint;
        if (player.getWindowPattern().isGridEmpty()) {
            constraint = new BorderConstraint(new ColorConstraint(new ValueConstraint(new EmptyConstraint())));
        } else {
            constraint = new ColorConstraint(new ValueConstraint(new EmptyConstraint()));
        }
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
                player.placeDie(d, cellIndex, constraint);
                this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException(DIE_INVALID_POSITION);
        }
        catch (DieAlreadyPlacedException e){
            throw new InvalidEffectResultException(DIE_ALREADY_PLACED_IN_THIS_TURN);
        }

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
