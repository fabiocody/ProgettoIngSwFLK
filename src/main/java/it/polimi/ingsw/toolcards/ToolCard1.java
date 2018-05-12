package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.server.Game;
import java.util.*;


public class ToolCard1 extends ToolCard {

    public ToolCard1(Game game) {
        super("Pinza Sgrossatrice", "Dopo aver scelto un dado, aumenta o diminuisci il valore del dado scelto di 1\nNon puoi cambiare un 6 in 1 o un 1 in 6", game);
    }

    /*
     *  JSON Format
     *  {
     *      "draftPoolIndex": <int>,
     *      "delta": <int>
     *  }
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check index
        List<Die> draftPool = this.getGame().getDiceGenerator().getDraftPool();
        Die d = draftPool.get(data.get("draftPoolIndex").getAsInt());
        int delta = data.get("delta").getAsInt();
        if (delta != 1 && delta != -1)
            throw new InvalidEffectResultException("Invalid delta");
        int newValue = d.getValue() + delta;
        if (d.getValue() == 1 && newValue == 6 || d.getValue() == 6 && newValue == 1)
            throw new InvalidEffectResultException("Cannot make a 1 into 6 or a 6 into 1");
        else d.setValue(newValue);
        this.setUsed();
    }

}
