package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.dice.Die;
import it.polimi.ingsw.server.Game;


public class ToolCard7 extends ToolCard {

    public ToolCard7(Game game) {
        super("Martelletto", "Tira nuovamente tutti i dadi della riserva\nQuesta carta può essere usata solo durante il tuo secondo turno, prima di scegliere il secondo dado", game);
    }

    /*
     *  JSON Format
     *  {}
     */
    public void effect(JsonObject data) throws InvalidEffectResultException {
        // TODO Check sul fatto che non abbia ancora tirato il dado
        if (!this.getGame().getTurnManager().isSecondHalfOfRound())
            throw new InvalidEffectResultException();
        this.getGame().getDiceGenerator().getDraftPool().forEach(Die::roll);
        this.setUsed();
    }

}
