package it.polimi.ingsw.tool_cards;


public abstract class ToolCard {

    private String name;
    private String description;
    private boolean used;

    public ToolCard(String name, String description) {
        this.name = name;
        this.description = description;
        this.used = false;
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

    public void setUsed() {
        this.used = true;
    }

    public abstract void effect();

}
