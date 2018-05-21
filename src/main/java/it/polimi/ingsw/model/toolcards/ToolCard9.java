package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.InvalidPlacementException;
import it.polimi.ingsw.model.placementconstraints.*;
import it.polimi.ingsw.server.*;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard9 extends ToolCard {

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard9(Game game) {
        super("Riga in Sughero", "Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
     *         &ensp;"cellX": &lt;int&gt;,<br>
     *         &ensp;"cellY": &lt;int&gt;<br>
     *     }
     * </code>
     *
     * @author Fabio Codiglioni
     * @param data the data the effect needs.
     * @throws InvalidEffectResultException thrown if the effect produces an invalid result.
     * @throws InvalidEffectArgumentException thrown if <code>data</code> contains any invalid values.
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        String nickname = data.get("player").getAsString();
        Player player = this.getGame().getPlayerForNickname(nickname);
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        int cellX = data.get("cellX").getAsInt();
        int cellY = data.get("cellY").getAsInt();
        int cellIndex = this.linearizeIndex(cellX, cellY);
        if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
            throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
        PlacementConstraint constraint = new ColorConstraint(new ValueConstraint(new OrthogonalConstraint(new EmptyConstraint())));
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(d, cellIndex, constraint);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            this.setUsed();
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException();
        }
    }

}
