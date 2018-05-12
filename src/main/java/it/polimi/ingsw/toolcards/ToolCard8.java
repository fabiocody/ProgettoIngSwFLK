package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.*;


public class ToolCard8 extends ToolCard {

    public ToolCard8(Game game) {
        super("Tenaglia a Rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\nSalta il tuo secondo turno in questo round", game);
    }

    /*
     *  {
     *      "player": <nickname: string>
     *  }
     */
    public void effect(JsonObject data) {
        // TODO
        Player player = this.getGame().getPlayerForNickname(data.get("player").getAsString());
        player.setSecondTurnToBeJumped(true);
        this.setUsed();
    }

}
