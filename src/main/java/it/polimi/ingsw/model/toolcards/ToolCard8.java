package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.*;


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
    public void effect(JsonObject data) {
        // TODO definire come viene detto al client di piazzare un altro dado
        Player player = this.getGame().getPlayerForNickname(data.get("player").getAsString());
        player.setSecondTurnToBeSkipped(true);
        this.setUsed();
        setChanged();
        notifyObservers("$useToolCard$");
    }

    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "requiredData");
        payload.addProperty("player", "$nickname$");
        return payload;
    }

}
