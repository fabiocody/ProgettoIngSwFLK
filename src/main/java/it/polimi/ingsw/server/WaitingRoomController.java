package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.LoginFailedException;
import it.polimi.ingsw.model.game.NicknameAlreadyUsedInGameException;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.WaitingRoom;
import it.polimi.ingsw.rmi.WaitingRoomAPI;
import java.util.*;


public class WaitingRoomController implements WaitingRoomAPI {

    private static WaitingRoomController instance;

    private WaitingRoomController() {}

    public static WaitingRoomController getInstance() {
        if (instance == null)
            instance = new WaitingRoomController();
        return instance;
    }

    @Override
    public UUID addPlayer(String nickname) throws LoginFailedException, NicknameAlreadyUsedInGameException {
        return WaitingRoom.getInstance().addPlayer(nickname);
    }

    @Override
    public void removePlayer(String nickname){
        WaitingRoom.getInstance().removePlayer(nickname);
    }

    @Override
    public List<Player> getWaitingPlayers() {
        return new Vector<>(WaitingRoom.getInstance().getWaitingPlayers());
    }

    @Override
    public void subscribeToWaitingRoomTimer(Observer observer) {
        WaitingRoom.getInstance().getTimer().addObserver(observer);
    }

    @Override
    public void unsubscribeFromWaitingRoomTimer(Observer observer) {
        WaitingRoom.getInstance().getTimer().deleteObserver(observer);
    }

    @Override
    public void subscribeToWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().addObserver(observer);
    }

    @Override
    public void unsubscribeFromWaitingRoom(Observer observer) {
        WaitingRoom.getInstance().deleteObserver(observer);
    }

}
