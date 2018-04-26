package it.polimi.ingsw.server;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


public class SagradaServer implements Observer {

    private WaitingRoom waitingRoom;
    private List<Game> games;

    public WaitingRoom getWaitingRoom() {
        if (this.waitingRoom == null) {
            this.waitingRoom = new WaitingRoom();
            this.waitingRoom.addObserver(this);
        }
        return this.waitingRoom;
    }

    List<Game> getGames() {
        if (this.games == null)
            this.games = new Vector<>();
        return this.games;
    }

    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            Game newGame = (Game) arg;
            getGames().add(newGame);
            new Thread(newGame).start();
        }
    }
}
