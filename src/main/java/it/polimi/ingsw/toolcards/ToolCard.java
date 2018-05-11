package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.Game;


public abstract class ToolCard {

    private String name;
    private String description;
    private boolean used;
    private Game game;

    public ToolCard(String name, String description, Game game) {
        this.name = name;
        this.description = description;
        this.used = false;
        this.game = game;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isUsed() {
        return this.used;
    }

    void setUsed() {
        this.used = true;
    }

    Game getGame() {
        return game;
    }

    int linearizeIndex(int i, int j) {
        return i*5 + j;
    }

    public abstract void effect(JsonObject data) throws InvalidEffectResultException;

    public boolean equals(Object obj) {
        if (!(obj instanceof ToolCard))
            return false;
        else
            return this.name.equals(((ToolCard) obj).getName());
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return this.getName() + " -> " + this.getDescription();
    }

}
