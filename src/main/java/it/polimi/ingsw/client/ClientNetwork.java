package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Logger;
import java.io.IOException;
import java.util.*;


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

    abstract UUID addPlayer(String nickname);
    abstract void choosePattern(int patternIndex);
    abstract boolean placeDie(int draftPoolIndex, int x, int y);
    abstract void nextTurn();
    abstract JsonObject requiredData(int cardIndex);
    abstract boolean useToolCard(int cardIndex, JsonObject requiredData);

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
