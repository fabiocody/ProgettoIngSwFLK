package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.server.*;


public class ToolCard6 extends ToolCard {

    private boolean dieRolled;

    public ToolCard6(Game game) {
        super("Pennello per Pasta Salda", "Dopo aver scelto un dado, tira nuovamente quel dado\nSe non puoi piazzarlo, riponilo nella riserva", game);
        this.dieRolled = false;
    }

    /*
     *  JSON Format
     *  {
     *      "player": <nickname: string>,
     *      "draftPoolIndex": <int>,
     *      "cellX": <int>,
     *      "cellY": <int>,
     *      "putAway": <bool>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        if (draftPoolIndex < 0 || draftPoolIndex >= this.getGame().getDiceGenerator().getDraftPool().size())
            throw new InvalidEffectArgumentException("Invalid draftPoolIndex: " + draftPoolIndex);
        if (!dieRolled) {
            rollDie(draftPoolIndex);
        } else {
            placeDie(data, draftPoolIndex);
        }
    }

    private void rollDie(int draftPoolIndex) {
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).roll();
        dieRolled = true;
    }

    private void placeDie(JsonObject data, int draftPoolIndex) throws InvalidEffectResultException, InvalidEffectArgumentException {
        if (!data.get("putAway").getAsBoolean()) {
            String nickname = data.get("player").getAsString();
            Player player = this.getGame().getPlayerForNickname(nickname);
            int cellX = data.get("cellX").getAsInt();
            int cellY = data.get("cellY").getAsInt();
            int cellIndex = this.linearizeIndex(cellX, cellY);
            if (cellIndex < 0 || cellIndex >= player.getWindowPattern().getGrid().length)
                throw new InvalidEffectArgumentException("Invalid cellIndex: " + cellIndex + " (" + cellX + ", " + cellY + ")");
            Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
            try {
                player.getWindowPattern().placeDie(die, cellIndex);
                this.getGame().getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
                dieRolled = false;
                this.setUsed();
            } catch (InvalidPlacementException e) {
                Cell cell = player.getWindowPattern().getCellAt(cellIndex);
                throw new InvalidEffectResultException("Invalid placement on cell at index " + cellIndex + " (" + cell + ") of die " + die);
            }
        } else {
            this.setUsed();
        }
    }

}
