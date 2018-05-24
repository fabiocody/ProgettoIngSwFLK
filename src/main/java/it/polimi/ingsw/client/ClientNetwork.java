package it.polimi.ingsw.client;

import java.io.IOException;
import java.util.Observable;
import java.util.UUID;


public abstract class ClientNetwork extends Observable {

    abstract void setup() throws IOException;
    abstract void teardown() throws IOException;

    abstract UUID addPlayer(String nickname);

}
