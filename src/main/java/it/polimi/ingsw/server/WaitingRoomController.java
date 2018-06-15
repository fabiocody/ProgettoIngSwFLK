package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.rmi.WaitingRoomAPI;
import it.polimi.ingsw.util.*;
import java.util.*;
import java.util.stream.Collectors;


public class WaitingRoomController implements WaitingRoomAPI, Observer {

    private static WaitingRoomController instance;

    private List<ServerNetwork> serverNetworks;

    private WaitingRoomController() {
        this.serverNetworks = new Vector<>();
        WaitingRoom.getInstance().addObserver(this);
        WaitingRoom.getInstance().getTimer().addObserver(this);
    }

    public static WaitingRoomController getInstance() {
        if (instance == null)
            instance = new WaitingRoomController();
        return instance;
    }

    public boolean addServerNetwork(ServerNetwork network) {
        return serverNetworks.add(network);
    }

    public boolean removeServerNetwork(ServerNetwork network) {
        return serverNetworks.remove(network);
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
    public List<String> getWaitingPlayers() {
        return WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    /*@Override
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
    }*/

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            if (arg instanceof List /*&& getUuid() != null*/) {
                Logger.debug("Updating waiting players");
                List<String> players = ((List<Player>) arg).stream()
                        .map(Player::getNickname)
                        .collect(Collectors.toList());
                serverNetworks.forEach(network -> network.updateWaitingPlayers(players));
            }
        } else if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith(NotificationsMessages.WAITING_ROOM)) {
                int tick = Integer.parseInt(stringArg.split(" ")[1]);
                Logger.debug("WR Timer tick (from update): " + tick);
                serverNetworks.forEach(network -> network.updateTimerTick(Methods.WR_TIMER_TICK, tick));
            }
        }
    }
}
