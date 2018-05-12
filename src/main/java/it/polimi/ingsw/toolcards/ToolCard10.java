package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.server.Game;


public class ToolCard10 extends ToolCard {

    public ToolCard10(Game game) {
        super("Tampone Diamantato", "Dopo aver scelto un dado, giralo sulla faccia opposta\n6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", game);
    }

    /*
     *  JSON Format
     *  {
     *      "draftPoolIndex": <int>
     *  }
     */
    public void effect(JsonObject data) {
        int draftPoolIndex = data.get("draftPoolIndex").getAsInt();
        Die die = this.getGame().getDiceGenerator().getDraftPool().get(draftPoolIndex);
        int value = die.getValue();
        die.setValue(7 - value);
        this.setUsed();
    }

}
