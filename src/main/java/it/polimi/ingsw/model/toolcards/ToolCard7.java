package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.server.Game;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard7 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard7(Game game) {
        super("Martelletto", "Tira nuovamente tutti i dadi della riserva\nQuesta carta può essere usata solo durante il tuo secondo turno, prima di scegliere il secondo dado", game);
    }

    /**
     * This method represents the effect of the Tool Card. It needs no data, so the argument can either be
     * an empty JSON object or <code>null</code>.
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check sul fatto che non abbia ancora tirato il dado
        if (!this.getGame().getTurnManager().isSecondHalfOfRound())
            throw new InvalidEffectResultException();
        this.getGame().getDiceGenerator().getDraftPool().forEach(Die::roll);
        this.setUsed();
    }

    @Override
    public JsonObject requiredData() {
        JsonObject payload = new JsonObject();
        payload.addProperty("method", "requiredData");
        return payload;
    }

}
