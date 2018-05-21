package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.server.*;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard11 extends ToolCard {

    private int state = 0;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard11(Game game) {
        super("Diluente per Pasta Salda", "Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto\nScegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
     *         &ensp;"newValue": &lt;int&gt;,<br>
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
        // TODO togliere switch
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        switch (state) {
            case 0:
                this.exchangeDie(draftPoolIndex);
                break;
            case 1:
                int newValue = data.get("newValue").getAsInt();
                if (newValue < 1 || newValue > 6)
                    throw new InvalidEffectArgumentException("Invalid newValue: " + newValue);
                this.chooseValue(draftPoolIndex, newValue);
                break;
            case 2:
                String nickname = data.get("player").getAsString();
                Player player = this.getGame().getPlayerForNickname(nickname);
                int cellX = data.get("cellX").getAsInt();
                int cellY = data.get("cellY").getAsInt();
                int cellIndex = this.linearizeIndex(cellX, cellY);
                if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
                    throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
                this.placeDie(player, draftPoolIndex, cellIndex);
                break;
        }
    }

    /**
     * This method handles the die exchanging part of this Tool Card effect.
     *
     * @param draftPoolIndex the index of the die from the Draft Pool.
     */
    private void exchangeDie(int draftPoolIndex) {
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        this.getGame().getDiceGenerator().putAway(d);
        this.getGame().getDiceGenerator().getDraftPool().set(draftPoolIndex, this.getGame().getDiceGenerator().draw());
        state = 1;
    }

    /**
     * This method handles the value choosing part of this Tool Card effect.
     *
     * @param draftPoolIndex the index of the die from the Draft Pool.
     * @param newValue the new value to be assigned to the die.
     */
    private void chooseValue(int draftPoolIndex, int newValue) {
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).setValue(newValue);
        state = 2;
    }

    /**
     * This method handles the die placement part of this Tool Card effect.
     *
     * @param player the player using this Tool Card.
     * @param draftPoolIndex the index of the die from the Draft Pool.
     * @param cellIndex the index of the cell you want to move the die to.
     * @throws InvalidEffectResultException thrown when the placement is invalid.
     */
    private void placeDie(Player player, int draftPoolIndex, int cellIndex) throws InvalidEffectResultException {
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.getWindowPattern().placeDie(die, cellIndex);
            this.state = 0;
            this.setUsed();
        } catch (InvalidPlacementException e) {
            Cell cell = player.getWindowPattern().getCellAt(cellIndex);
            throw new InvalidEffectResultException("Invalid placement on cell at index " + cellIndex + " (" + cell + ") of die " + die);
        }
    }

}
