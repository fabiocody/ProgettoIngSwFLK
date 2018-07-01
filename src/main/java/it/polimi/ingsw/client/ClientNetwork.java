package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.shared.util.Constants;
import it.polimi.ingsw.shared.util.JsonFields;
import it.polimi.ingsw.shared.util.Logger;
import java.io.IOException;
import java.util.*;


public abstract class ClientNetwork extends Observable {

    private static ClientNetwork instance;

    private String host;
    private int port;

    String nickname;
    UUID uuid;

    private Timer probeTimer;
    boolean gameEnding = false;

    ClientNetwork(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        Logger.setDebugActive(debug);
    }

    public static ClientNetwork getInstance() {
        return instance;
    }

    public static void setInstance(ClientNetwork clientNetwork) {
        instance = clientNetwork;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    public abstract void setup() throws IOException;
    public abstract void teardown() throws IOException;

    public abstract UUID addPlayer(String nickname);
    public abstract void choosePattern(int patternIndex);
    public abstract JsonObject placeDie(int draftPoolIndex, int x, int y);
    public abstract void nextTurn();
    public abstract JsonObject requiredData(int cardIndex);
    public abstract JsonObject useToolCard(int cardIndex, JsonObject requiredData);

    void rescheduleProbeTimer() {
        if (this.probeTimer != null) {
            this.probeTimer.cancel();
            this.probeTimer.purge();
        }
        this.probeTimer = new Timer(true);
        this.probeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gameEnding)
                    connectionError();
            }
        }, Constants.PROBE_TIMEOUT * 2000);
    }

    void connectionError() {
        connectionError(null);
    }

    void connectionError(Throwable e) {
        Logger.connectionLost();
        Logger.printStackTraceConditionally(e);
        JsonObject obj = new JsonObject();
        obj.addProperty(JsonFields.EXIT, true);
        setChanged();
        notifyObservers(obj);
        //System.exit(Constants.EXIT_ERROR);
    }

}
