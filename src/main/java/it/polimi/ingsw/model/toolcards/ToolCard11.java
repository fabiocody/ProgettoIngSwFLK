package it.polimi.ingsw.model.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.DieAlreadyPlacedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import java.util.*;
import static it.polimi.ingsw.shared.util.Constants.TOOL_CARD_11_NAME;
import static it.polimi.ingsw.shared.util.InterfaceMessages.DIE_INVALID_POSITION;


/**
 * @author Fabio Codiglioni
 */
public class ToolCard11 extends ToolCard {

    private final List<String> requiredDataFirst = Arrays.asList(JsonFields.DRAFT_POOL_INDEX, JsonFields.CONTINUE);
    private final List<String> requiredDataSecond = Arrays.asList(JsonFields.NEW_VALUE, JsonFields.TO_CELL_X, JsonFields.TO_CELL_Y);

    private int state = 0;
    private int draftPoolIndex = Constants.INDEX_CONSTANT;

    /**
     * This constructor initializes the card with its name and description.
     *
     * @author Fabio Codiglioni
     * @param game the game object this card is part of.
     */
    public ToolCard11(Game game) {
        super(TOOL_CARD_11_NAME, "Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto\nScegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento", game);
    }

    /**
     * This method represents the effect of the Tool Card.
     * It takes in a JSON object formatted as follows: <br>
     * <code>
     *     { <br>
     *         &ensp;"player": &lt;nickname: string&gt;,<br>
     *         &ensp;"draftPoolIndex": &lt;int&gt;,<br>
     *         &ensp;"newValue": &lt;int&gt;,<br>
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
        if (state == 0) {
            draftPoolIndex = data.get(JsonFields.DRAFT_POOL_INDEX).getAsInt();
            if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
                throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
            this.exchangeDie(draftPoolIndex);

        } else if (state == 1) {
            int newValue = data.get(JsonFields.NEW_VALUE).getAsInt();
            if (newValue < 1 || newValue > 6)
                throw new InvalidEffectArgumentException("Invalid newValue: " + newValue);
            this.chooseValue(draftPoolIndex, newValue);
            String nickname = data.get(JsonFields.PLAYER).getAsString();
            Player player = this.getGame().getPlayer(nickname);
            int cellX = data.get(JsonFields.TO_CELL_X).getAsInt();
            int cellY = data.get(JsonFields.TO_CELL_Y).getAsInt();
            this.placeDie(player, draftPoolIndex, cellX, cellY);

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
    }

    /**
     * Handles the die placement part of this Tool Card effect
     *
     * @param player the Player object representing the player using the Tool Card
     * @param draftPoolIndex the index of the die from the Draft Pool
     * @param cellX the column you want to place the die on
     * @param cellY the row you want to place the die on
     * @throws InvalidEffectResultException when the placement is invalid
     */
    private void placeDie(Player player, int draftPoolIndex, int cellX, int cellY) throws InvalidEffectResultException {
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        try {
            player.placeDie(die, cellX, cellY);
            this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
            this.state = 0;
        } catch (InvalidPlacementException | DieAlreadyPlacedException e) {
            this.state = 0;
            throw new InvalidEffectResultException(DIE_INVALID_POSITION);
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
        if(state != 0) {
            if (!this.isUsed()) {
                player.setFavorTokens(player.getFavorTokens() - 1);
            } else {
                player.setFavorTokens(player.getFavorTokens() - 2);
            }
            this.setUsed();
            player.setToolCardUsedThisTurn(true);
        }
        state = 0;
    }

    @Override
    public List<String> getRequiredData() {
        if (state == 0)
            return new ArrayList<>(requiredDataFirst);
        return new ArrayList<>(requiredDataSecond);
    }

}