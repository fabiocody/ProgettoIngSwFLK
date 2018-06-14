package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.rmi.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;


public class RMIClient extends ClientNetwork {

    private GameAPI gameAPI;
    private WaitingRoomAPI waitingRoomAPI;

    RMIClient(String host, int port, boolean debug) {
        super(host, port, debug);
    }

    @Override
    void setup() throws RemoteException {

    }

    @Override
    void teardown() throws IOException {

    }

    @Override
    UUID addPlayer(String nickname) {
        return null;
    }

    @Override
    void choosePattern(int patternIndex) {

    }

    @Override
    boolean placeDie(int draftPoolIndex, int x, int y) {
        return false;
    }

    @Override
    void nextTurn() {

    }

    @Override
    JsonObject requiredData(int cardIndex) {
        return null;
    }

    @Override
    boolean useToolCard(int cardIndex, JsonObject requiredData) {
        return false;
    }
}
