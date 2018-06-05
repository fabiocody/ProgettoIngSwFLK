package it.polimi.ingsw.client;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Observable;
import java.util.UUID;


public abstract class ClientNetwork extends Observable {

    abstract void setup() throws IOException;
    abstract void teardown() throws IOException;

    abstract UUID addPlayer(String nickname);
    abstract void choosePattern(int patternIndex);
    abstract boolean placeDie(int draftPoolIndex, int x, int y);
    abstract int nextTurn();

    abstract JsonObject requiredData(int cardIndex);
    abstract boolean useToolCard(int cardIndex, JsonObject requiredData);
}
