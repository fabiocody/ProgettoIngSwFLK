package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.Game;


public class ToolCard8 extends ToolCard {

    public ToolCard8(Game game) {
        super("Tenaglia a Rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\nSalta il tuo secondo turno in questo round", game);
    }

    public void effect(JsonObject data) {
        // TODO
        this.setUsed();
    }

}
