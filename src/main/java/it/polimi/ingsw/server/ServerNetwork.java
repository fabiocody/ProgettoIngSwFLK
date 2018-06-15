package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.util.*;
import java.util.*;


public abstract class ServerNetwork implements Observer {

    String nickname;
    UUID uuid;
    GameController gameController;

    Thread probeThread;
    boolean probed = true;

    abstract void updatePlayersList();
    abstract void updateToolCards();
    abstract void sendPublicObjectiveCards();
    abstract void updateWindowPatterns();
    abstract void updateFavorTokens();
    abstract void updateDraftPool();
    abstract void updateRoundTrack();
    abstract void turnManagement();
    abstract void updateFinalScores();

    abstract void updateTimerTick(Methods method, int tick);
    abstract void updateWaitingPlayers(List<String> players);
    abstract void setupGame();
    abstract void fullUpdate();
    abstract void sendProbe();
    abstract void notifyDisconnectedUser();

    JsonObject createWindowPatternJSON(WindowPattern wp) {
        JsonObject wpJSON = new JsonObject();
        wpJSON.addProperty(JsonFields.DIFFICULTY, wp.getDifficulty());
        JsonArray grid = new JsonArray();
        for (Cell c : wp.getGrid()) {
            JsonObject cellJSON = new JsonObject();
            cellJSON.addProperty(JsonFields.COLOR, c.getCellColor() != null ? c.getCellColor().toString() : null);
            cellJSON.addProperty(JsonFields.VALUE, c.getCellValue());
            JsonObject die = null;
            if (c.getPlacedDie() != null) {
                die = new JsonObject();
                die.addProperty(JsonFields.COLOR, c.getPlacedDie().getColor().toString());
                die.addProperty(JsonFields.VALUE, c.getPlacedDie().getValue());
            }
            cellJSON.add(JsonFields.DIE, die);
            grid.add(cellJSON);
        }
        wpJSON.add(JsonFields.GRID, grid);
        wpJSON.addProperty(JsonFields.CLI_STRING, wp.toString());
        return wpJSON;
    }

    void probeCheck() {
        while (Constants.INDEX_CONSTANT == Constants.INDEX_CONSTANT) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                break;
            }
            Logger.debug("Checking probe for " + nickname);
            if (!probed) {
                Logger.error("Probe error");
                this.notifyDisconnectedUser();
                Thread.currentThread().interrupt();
            }
            probed = false;
            this.sendProbe();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SagradaServer) {
            WaitingRoomController.getInstance().removeServerNetwork(this);
            this.gameController = (GameController) arg;
            this.gameController.addServerNetwork(this);
            this.setupGame();
        }
    }

}
