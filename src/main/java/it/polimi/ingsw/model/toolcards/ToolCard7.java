package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_7_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.FIRST_HALF_OF_ROUND;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard7 extends ToolCard {

    private final List<String> requiredData = new ArrayList<>();

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard7(Game game) {
        super(TOOL_CARD_7_NAME, "Tira nuovamente tutti i dadi della riserva\nQuesta carta può essere usata solo durante il tuo secondo turno, prima di scegliere il secondo dado", game);
    }

    /**
     * This method represents the effect of the Tool Card. It needs no data, so the argument can either be
     * an empty JSON object or <code>null</code>.
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     */
    @Override
    public void effect(JsonObject data) throws InvalidEffectResultException {
        if (!this.getGame().getTurnManager().isSecondHalfOfRound())
            throw new InvalidEffectResultException(FIRST_HALF_OF_ROUND);
        this.getGame().getDiceGenerator().getDraftPool().forEach(Die::roll);
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
