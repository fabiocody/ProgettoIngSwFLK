package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.patterncards.InvalidPlacementException;
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
     *      "x": <int>,
     *      "y": <int>,
     *      "putAway": <bool>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check index
        if (!dieRolled) rollDie(data);
        else placeDie(data);
    }

    private void rollDie(JsonObject data) {
        int index = data.get("draftPoolIndex").getAsInt();
        this.getGame().getDiceGenerator().getDraftPool().get(index).roll();
        dieRolled = true;
    }

    private void placeDie(JsonObject data) throws InvalidEffectResultException {
        if (!data.get("putAway").getAsBoolean()) {
            int x = data.get("x").getAsInt();
            int y = data.get("y").getAsInt();
            int index = data.get("draftPoolIndex").getAsInt();
            Die die = this.getGame().getDiceGenerator().getDraftPool().get(index);
            Player player = this.getGame().getPlayerForNickname(data.get("player").getAsString());
            try {
                player.getWindowPattern().placeDie(die, linearizeIndex(x, y));
                this.getGame().getDiceGenerator().drawDieFromDraftPool(index);
                dieRolled = false;
                this.setUsed();
            } catch (InvalidPlacementException e) {
                throw new InvalidEffectResultException();
            }
        }
    }

}
