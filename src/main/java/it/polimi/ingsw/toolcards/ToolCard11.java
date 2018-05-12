package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
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
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check index
        // TODO togliere switch
        switch (state) {
            case 0:
                exchangeDie(data);
                break;
            case 1:
                chooseValue(data);
                break;
            case 2:
                placeDie(data);
                break;
        }
    }

    private void exchangeDie(JsonObject data) {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        Die d = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        this.getGame().getDiceGenerator().putAway(d);
        this.getGame().getDiceGenerator().getDraftPool().set(draftPoolIndex, this.getGame().getDiceGenerator().draw());
        state = 1;
    }

    private void chooseValue(JsonObject data) {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex).setValue(data.get("newValue").getAsInt());
        state = 2;
    }

    private void placeDie(JsonObject data) throws InvalidEffectResultException {
        Player player = this.getGame().getPlayerForNickname(data.get("player").getAsString());
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        int x = data.get("cellX").getAsInt();
        int y = data.get("cellY").getAsInt();
        try {
            player.getWindowPattern().placeDie(die, linearizeIndex(x, y));
            this.state = 0;
            this.setUsed();
        } catch (InvalidPlacementException e) {
            throw new InvalidEffectResultException();
        }
    }

}
