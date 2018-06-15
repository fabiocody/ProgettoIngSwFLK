package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Logger;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public abstract class ClientNetwork extends Observable {

    private String host;
    private int port;

    private String nickname;
    private UUID uuid;

    private Timer probeTimer;

    ClientNetwork(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        Logger.setDebugActive(debug);
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }


    String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    UUID getUuid() {
        return uuid;
    }

    void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    abstract void setup() throws IOException;
    abstract void teardown() throws IOException;

    abstract UUID addPlayer(String nickname) throws RemoteException;
    abstract void choosePattern(int patternIndex) throws RemoteException;
    abstract boolean placeDie(int draftPoolIndex, int x, int y) throws RemoteException;
    abstract void nextTurn() throws RemoteException;
    abstract JsonObject requiredData(int cardIndex) throws RemoteException;
    abstract boolean useToolCard(int cardIndex, JsonObject requiredData) throws RemoteException;

    void rescheduleProbeTimer() {
        if (this.probeTimer != null) {
            this.probeTimer.cancel();
            this.probeTimer.purge();
        }
        this.probeTimer = new Timer(true);
        this.probeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.connectionLost();
                System.exit(Constants.EXIT_ERROR);
            }
        }, Constants.PROBE_TIMEOUT * 2000);
    }

}
