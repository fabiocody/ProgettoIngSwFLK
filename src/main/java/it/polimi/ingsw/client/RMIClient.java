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

    public RMIClient(String host, int port, boolean debug) {
        super(host, port, debug);
    }

    @Override
    public void setup() throws IOException {
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
    public void teardown() {
        server = null;
    }

    @Override
    public UUID addPlayer(String nickname) {
        try {
            setUuid(server.addPlayer(nickname));
        } catch (RemoteException e) {
            connectionError(e);
        }
        return getUuid();
    }

    @Override
    public boolean choosePattern(int patternIndex) {
        try {
            return server.choosePattern(patternIndex);
        } catch (RemoteException e) {
            connectionError(e);
            return false;
        }
    }

    @Override
    public JsonObject placeDie(int draftPoolIndex, int x, int y) {
        try {
            return getJsonParser().parse(server.placeDie(draftPoolIndex, x, y)).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError(e);
            return null;
        }
    }

    @Override
    public JsonObject requiredData(int cardIndex) {
        try {
            return getJsonParser().parse(server.requiredData(cardIndex)).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError(e);
            return null;
        }
    }

    @Override
    public JsonObject useToolCard(int cardIndex, JsonObject data) {
        try {
            return getJsonParser().parse(server.useToolCard(cardIndex, data.toString())).getAsJsonObject();
        } catch (RemoteException e) {
            connectionError(e);
            return null;
        }
    }

    @Override
    public void cancelToolCardUsage(int cardIndex) {
        try {
            server.cancelToolCardUsage(cardIndex);
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    @Override
    public void nextTurn() {
        try {
            server.nextTurn();
        } catch (RemoteException e) {
            connectionError(e);
        }
    }

    @Override
    public void probe() {
        try {
            server.probe();
            this.rescheduleProbeTimer();
        } catch (RemoteException | NullPointerException e) {
            connectionError(e);
        }
    }

    @Override
    public void update(String jsonString) {
        JsonObject payload = getJsonParser().parse(jsonString).getAsJsonObject();
        if (payload.get(JsonFields.METHOD).getAsString().equals(Methods.FINAL_SCORES.getString()))
            gameEnding = true;
        this.setChanged();
        this.notifyObservers(payload);
    }

    @Override
    public void reconnect(String privateObjString) {
        JsonObject privateObjectiveCard = getJsonParser().parse(privateObjString).getAsJsonObject();
        JsonObject payload = new JsonObject();
        payload.addProperty(JsonFields.METHOD, Methods.ADD_PLAYER.getString());
        payload.addProperty(JsonFields.RECONNECTED, true);
        payload.add(JsonFields.PRIVATE_OBJECTIVE_CARD, privateObjectiveCard);
        setChanged();
        notifyObservers(payload);
    }

}
