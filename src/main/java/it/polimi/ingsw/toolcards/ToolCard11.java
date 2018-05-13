package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.server.*;


public class ToolCard11 extends ToolCard {

    private int state = 0;

    public ToolCard11(Game game) {
        super("Diluente per Pasta Salda", "Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto\nScegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento", game);
    }

    /*
     *  JSON Format
     *  {
     *      "player": <nickname: string>,
     *      "draftPoolIndex": <int>,
     *      "newValue": <int>,
     *      "cellX": <int>,
     *      "cellY": <int>,
     *  }
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

    private void exchangeDie(int draftPoolIndex) {
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        this.getGame().getDiceGenerator().putAway(d);
        this.getGame().getDiceGenerator().getDraftPool().set(draftPoolIndex, this.getGame().getDiceGenerator().draw());
        state = 1;
    }

    private void chooseValue(int draftPoolIndex, int newValue) {
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).setValue(newValue);
        state = 2;
    }

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
