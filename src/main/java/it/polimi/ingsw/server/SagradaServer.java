package it.polimi.ingsw.server;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


// Main server class
public class SagradaServer implements Observer {

    private WaitingRoom waitingRoom;
    private List<Game> games;

    SagradaServer() {
        WaitingRoom.getInstance().addObserver(this);
    }

    List<Game> getGames() {
        if (this.games == null)
            this.games = new Vector<>();
        return this.games;
    }

    // Observer method, instantiate and start a game when called
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            Game newGame = (Game) arg;
            getGames().add(newGame);
            new Thread(newGame).start();
        }
    }
}
