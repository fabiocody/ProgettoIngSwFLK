package it.polimi.ingsw.toolcards;

import com.google.gson.JsonObject;
import it.polimi.ingsw.patterncards.*;
import it.polimi.ingsw.placementconstraints.PlacementConstraint;
import it.polimi.ingsw.server.*;


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

    int linearizeIndex(int x, int y) {
        return y*5 + x;
    }

    void moveDie(Player player, int fromIndex, int toIndex, PlacementConstraint constraint) throws InvalidEffectResultException {
        try {
            player.getWindowPattern().moveDie(fromIndex, toIndex, constraint);
        } catch (InvalidPlacementException e) {
            Cell fromCell = player.getWindowPattern().getCellAt(fromIndex);
            Cell toCell = player.getWindowPattern().getCellAt(toIndex);
            throw new InvalidEffectResultException("Invalid movement from cell at index " + fromIndex + " (with die " + fromCell.getPlacedDie() + ") to cell at index " + toIndex + " (" + toCell + ")");
        }
    }

    public abstract void effect(JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException;

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
