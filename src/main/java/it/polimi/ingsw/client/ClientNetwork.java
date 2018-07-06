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

    private String nickname;
    private UUID uuid;

    private Timer probeTimer;
    boolean gameEnding = false;

    ClientNetwork(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        Logger.setDebug(debug);
    }

    public static ClientNetwork getInstance() {
        return instance;
    }

    public static void setInstance(ClientNetwork network) {
        instance = network;
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

    public abstract void setup() throws IOException;
    public abstract void teardown() throws IOException;

    public abstract UUID addPlayer(String nickname);
    public abstract boolean choosePattern(int patternIndex);
    public abstract JsonObject placeDie(int draftPoolIndex, int x, int y);
    public abstract void nextTurn();
    public abstract JsonObject requiredData(int cardIndex);
    public abstract JsonObject useToolCard(int cardIndex, JsonObject requiredData);
    public abstract void cancelToolCardUsage(int cardIndex);

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
                    connectionError("rescheduleProbeTimer");
            }
        }, Constants.PROBE_TIMEOUT * 2000);
    }

    void connectionError(String message, Throwable e) {
        Logger.connectionLost();
        if (message != null) Logger.conditionalError(message);
        Logger.printStackTraceConditionally(e);
        JsonObject obj = new JsonObject();
        obj.addProperty(JsonFields.EXIT_ERROR, true);
        setChanged();
        notifyObservers(obj);
    }

    void connectionError(String message) {
        connectionError(message, null);
    }

    void connectionError(Throwable e) {
        connectionError(null, e);
    }

}
