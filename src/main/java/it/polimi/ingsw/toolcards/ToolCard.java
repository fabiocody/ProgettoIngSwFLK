package it.polimi.ingsw.toolcards;


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

    public boolean equals(Object obj) {
        if (!(obj instanceof ToolCard))
            return false;
        else
            return this.name.equals(((ToolCard) obj).getName());
    }
}
