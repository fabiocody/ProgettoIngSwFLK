package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.Game;


public class ToolCard12 extends ToolCard {

    public ToolCard12(Game game) {
        super("Taglierina Manuale", "Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round\nDevi rispettare tutte le restrizioni di piazzamento", game);
    }

    public void effect(JsonObject data) {
        // TODO
        this.setUsed();
    }

}
