package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.util.*;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class RMIClient extends ClientNetwork implements ClientAPI {

    private ServerAPI server;
    private JsonParser jsonParser;

    RMIClient(String host, int port, boolean debug) {
        super(host, port, debug);
        this.jsonParser = new JsonParser();
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
        // TODO
    }

    @Override
    UUID addPlayer(String nickname) {
        try {
            uuid = server.addPlayer(nickname);
        } catch (RemoteException e) {
            connectionError();
        }
        return uuid;
    }

    @Override
    void choosePattern(int patternIndex) {
        try {
            server.choosePattern(patternIndex);
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    boolean placeDie(int draftPoolIndex, int x, int y) {
        try {
            return server.placeDie(draftPoolIndex, x, y);
        } catch (RemoteException e) {
            connectionError();
            return false;
        }
    }

    @Override
    void nextTurn() {
        try {
            server.nextTurn();
        } catch (RemoteException e) {
            connectionError();
        }
    }

    @Override
    JsonObject requiredData(int cardIndex) {
        try {
            return server.requiredData(cardIndex);
        } catch (RemoteException e) {
            connectionError();
            return null;
        }
    }

    @Override
    boolean useToolCard(int cardIndex, JsonObject requiredData) {
        try {
            return server.useToolCard(cardIndex, requiredData);
        } catch (RemoteException e) {
            connectionError();
            return false;
        }
    }

    @Override
    public void probe() {
        try {
            server.probe();
        } catch (RemoteException e) {
            connectionError();
        }
        this.rescheduleProbeTimer();
    }

    private void connectionError() {
        Logger.connectionLost(nickname);
        System.exit(Constants.EXIT_ERROR);
    }

    @Override
    public void update(String jsonString) throws RemoteException {
        JsonObject payload = jsonParser.parse(jsonString).getAsJsonObject();
        this.setChanged();
        this.notifyObservers(payload);
    }

}
