package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.util.Methods;
import java.util.*;


public abstract class ServerNetwork implements Observer {

    String nickname;
    UUID uuid;
    GameController gameController;

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
    abstract void updateWaitingPlayersList(List<Player> players);
    abstract void setupGame();
    abstract void fullUpdate();

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
