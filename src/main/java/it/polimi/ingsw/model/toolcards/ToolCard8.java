package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_8_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard8 extends ToolCard {

    private final List<String> requiredData = Arrays.asList(JsonFields.DRAFT_POOL_INDEX, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y, JsonFields.SECOND_DIE_PLACEMENT);

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard8(Game game) {
        super(TOOL_CARD_8_NAME, "Dopo il tuo primo turno scegli immediatamente un altro dado\nSalta il tuo secondo turno in questo round", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     */
    @Override
    public void effect(JsonObject data) throws InvalidEffectArgumentException, InvalidEffectResultException  {
        Player player = this.getGame().getPlayer(data.get(JsonFields.PLAYER).getAsString());
        player.setSecondTurnToBeSkipped(true);
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int cellX= data.get(JsonFields.TO_CELL_X).getAsInt();
        int cellY= data.get(JsonFields.TO_CELL_Y).getAsInt();
        int cellIndex = this.linearizeIndex(cellX, cellY);
        if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException(InterfaceMessages.DIE_INVALID_POSITION);
        this.placeDie(player, draftPoolIndex, cellIndex);
    }

    /**
     * @param player the player who used the tool card
     * @param draftPoolIndex index of the die in the draftpool
     * @param cellIndex the index of the cell where the die will be placed
     * @throws InvalidEffectResultException
     */
    private void placeDie(Player player, int draftPoolIndex, int cellIndex) throws InvalidEffectResultException {
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(die, cellIndex);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException(InterfaceMessages.DIE_INVALID_POSITION);
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
