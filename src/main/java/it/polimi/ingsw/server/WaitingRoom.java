package it.polimi.ingsw.server;

import java.util.ArrayList;
import java.util.List;


public class WaitingRoom {

    private List<String> nicknames;

    public List<String> getNicknames() {
        if (this.nicknames == null)
            this.nicknames = new ArrayList<>();
        return this.nicknames;
    }

    public boolean addPlayer(String nickname) {
        if (this.getNicknames().size() >= 4 || this.getNicknames().contains(nickname))
            return false;
        this.getNicknames().add(nickname);
        return true;
    }

    public Game createGame() {
        List<String> currentNicknames = this.getNicknames().subList(0, 4);
        this.getNicknames().removeAll(currentNicknames);
        return new Game(currentNicknames);
    }

}
