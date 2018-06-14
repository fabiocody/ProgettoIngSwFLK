package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.Cell;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.JsonFields;
import it.polimi.ingsw.util.Methods;
import it.polimi.ingsw.util.NotificationsMessages;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard8 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard8(Game game) {
        super("Tenaglia a Rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\nSalta il tuo secondo turno in questo round", game);
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
    public void effect(JsonObject data) throws InvalidEffectArgumentException, InvalidEffectResultException  {
        // TODO definire come viene detto al client di piazzare un altro dado
        Player player = this.getGame().getPlayerForNickname(data.get(JsonFields.PLAYER).getAsString());
        player.setSecondTurnToBeSkipped(true);
        int draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
        int cellX= data.get(JsonFields.TO_CELL_X).getAsInt();
        int cellY= data.get(JsonFields.TO_CELL_Y).getAsInt();
        int cellIndex = this.linearizeIndex(cellX, cellY);
        if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
        this.placeDie(player, draftPoolIndex, cellIndex);
        setChanged();
        notifyObservers(NotificationsMessages.USE_TOOL_CARD);
    }

    /**
     * This method is used to send a JsonObject containing the fields that the user will have to fill to use this tool card
     *
     * @author Kai de Gast
     * @return JsonObject containing the required fields filled with momentary constants
     */
    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.REQUIRED_DATA.getString());
        JsonObject data = new JsonObject();
        data.addProperty(JsonFields.DRAFT_POOL_INDEX, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.SECOND_DIE_PLACEMENT, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    private void placeDie(Player player, int draftPoolIndex, int cellIndex) throws InvalidEffectResultException {
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(die, cellIndex);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
        } catch (InvalidPlacementException e) {
            Cell cell = player.getWindowPattern().getCellAt(cellIndex);
            throw new InvalidEffectResultException("Invalid placement on cell at index " + cellIndex + " (" + cell + ") of die " + die);
        }
    }


}
