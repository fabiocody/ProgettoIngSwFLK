package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.game.LoginFailedException;
import it.polimi.ingsw.model.game.NicknameAlreadyUsedInGameException;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.util.Logger;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.*;


public class RMIClient extends ClientNetwork {

    private ServerAPI server;

    RMIClient(String host, int port, boolean debug) {
        super(host, port, debug);
    }

    @Override
    void setup() throws IOException {
        try {
            ServerAPI welcomeServer = (ServerAPI) Naming.lookup(RMINames.SERVER);
            server = welcomeServer.connect();
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
        /*Logger.debug("addPlayer called");
        this.setNickname(nickname);
        try {
            this.setUuid(server.addPlayer(nickname));
            Logger.debug("Login successful");
            // TODO Update waiting players
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginFailedException e) {
            return null;
        } catch (NicknameAlreadyUsedInGameException e) {
            e.printStackTrace();
        }*/
        return this.getUuid();
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
