package it.polimi.ingsw.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.util.Logger;
import it.polimi.ingsw.util.NotificationsMessages;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class RMIClient extends ClientNetwork implements ClientAPI {

    private ServerAPI server;

    RMIClient(String host, int port, boolean debug) {
        super(host, port, debug);
    }

    @Override
    void setup() throws IOException {
        try {
            ServerAPI welcomeServer = (ServerAPI) Naming.lookup(RMINames.SERVER);
            ClientAPI clientRemote = (ClientAPI) UnicastRemoteObject.exportObject(this, 0);
            server = welcomeServer.connect(clientRemote);
            if (server == null)
                throw new IOException();
        } catch (MalformedURLException e) {
            Logger.error("Malformed URL");
            throw new IOException();
        } catch (NotBoundException e) {
            Logger.error("Il riferimento passato non è associato a nulla!");
            throw new IOException();
        }
    }

    @Override
    void teardown() throws IOException {

    }

    @Override
    UUID addPlayer(String nickname) {
        UUID uuid = null;
        try {
            uuid = server.addPlayer(nickname);
        } catch (RemoteException e) {
            Logger.error("Connection error");
        }
        setUuid(uuid);
        return uuid;
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

    @Override
    public void wrTimerTick(int tick) {
        String tickString = String.valueOf(tick);
        this.setChanged();
        this.notifyObservers(Arrays.asList(NotificationsMessages.WR_TIMER_TICK, tickString));
    }

    @Override
    public void updateWaitingPlayers(List<String> players) {
        JsonArray waitingPlayers = new JsonArray();
        players.forEach(waitingPlayers::add);
        this.setChanged();
        this.notifyObservers(waitingPlayers);
    }
}
