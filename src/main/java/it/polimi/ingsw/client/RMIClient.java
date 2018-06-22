package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.shared.rmi.*;
import it.polimi.ingsw.shared.util.*;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
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
            Registry registry = LocateRegistry.getRegistry(getHost(), getPort());
            ServerAPI welcomeServer = (ServerAPI) registry.lookup(Constants.SERVER_RMI_NAME);
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
    void teardown() {
        // TODO
    }

    @Override
    UUID addPlayer(String nickname) {
        try {
            uuid = server.addPlayer(nickname);
        } catch (RemoteException e) {
            connectionError(e);
        }
        return uuid;
    }

    @Override
    void choosePattern(int patternIndex) {
        try {
            server.choosePattern(patternIndex);
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    @Override
    boolean placeDie(int draftPoolIndex, int x, int y) {
        try {
            return server.placeDie(draftPoolIndex, x, y);
        } catch (RemoteException e) {
            connectionError(e);
            return false;
        }
    }

    @Override
    void nextTurn() {
        try {
            server.nextTurn();
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    @Override
    JsonObject requiredData(int cardIndex) {
        try {
            return server.requiredData(cardIndex);
        } catch (RemoteException e) {
            connectionError(e);
            return null;
        }
    }

    @Override
    boolean useToolCard(int cardIndex, JsonObject requiredData) {
        try {
            return server.useToolCard(cardIndex, requiredData);
        } catch (RemoteException e) {
            connectionError(e);
            return false;
        }
    }

    @Override
    public void probe() {
        try {
            server.probe();
        } catch (RemoteException e) {
            connectionError(e);
        }
        this.rescheduleProbeTimer();
    }

    private void connectionError(Throwable e) {
        Logger.connectionLost(nickname);
        e.printStackTrace();
        System.exit(Constants.EXIT_ERROR);
    }

    @Override
    public void update(String jsonString) {
        JsonObject payload = jsonParser.parse(jsonString).getAsJsonObject();
        this.setChanged();
        this.notifyObservers(payload);
    }

}
