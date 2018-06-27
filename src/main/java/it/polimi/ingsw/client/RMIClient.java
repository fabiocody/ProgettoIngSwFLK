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
            Logger.error(Constants.SERVER_RMI_NAME + " not bound");
            throw new IOException();
        }
    }

    @Override
    void teardown() {
        server = null;
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
    JsonObject placeDie(int draftPoolIndex, int x, int y) {
        try {
            return jsonParser.parse(server.placeDie(draftPoolIndex, x, y)).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError();
            return null;
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
            return jsonParser.parse(server.requiredData(cardIndex)).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError();
            return null;
        }
    }

    @Override
    JsonObject useToolCard(int cardIndex, JsonObject requiredData) {
        try {
            return jsonParser.parse(server.useToolCard(cardIndex, requiredData.toString())).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError();
            return null;
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
    public void update(String jsonString) {
        JsonObject payload = jsonParser.parse(jsonString).getAsJsonObject();
        this.setChanged();
        this.notifyObservers(payload);
    }

}
