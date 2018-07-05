package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Methods;

import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_3_NAME;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard3 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard3(Game game) {
        super(TOOL_CARD_3_NAME, "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\nDevi rispettare tutte le altre restrizioni di piazzamento", game);
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
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        PlacementConstraint constraint;
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
        constraint = (player.getWindowPattern().checkIfOnlyOneDie()) ?
                new BorderConstraint(new ColorConstraint(new EmptyConstraint())) : new PositionConstraint(new ColorConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        this.moveDie(player, fromIndex, toIndex, constraint);
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
        data.addProperty(JsonFields.FROM_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.FROM_CELL_Y, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_X, Constants.INDEX_CONSTANT);
        data.addProperty(JsonFields.TO_CELL_Y, Constants.INDEX_CONSTANT);
        payload.add(JsonFields.DATA, data);
        return payload;
    }

    @Override
    public void cancel(Player player){
        // Nothing to cancel
    }

}
