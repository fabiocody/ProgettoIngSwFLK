package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.shared.rmi.WaitingRoomAPI;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class WaitingRoomController implements WaitingRoomAPI, Observer {

    private static WaitingRoomController instance;

    private List<ServerNetwork> serverNetworks;
    private final Object serverNetworksLock = new Object();
    private boolean serverNetworksBusy = false;

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

    void addServerNetwork(ServerNetwork network) {
        synchronized (serverNetworksLock) {
            new Thread(() -> {
                synchronized (serverNetworksLock) {
                    Logger.debug("Attempt to add ServerNetwork");
                    while (serverNetworksBusy) {
                        try {
                            serverNetworksLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    serverNetworks.add(network);
                    Logger.debug("ServerNetwork added");
                }
            }).start();
        }
    }

    void removeServerNetwork(ServerNetwork network) {
        synchronized (serverNetworksLock) {
            new Thread(() -> {
                synchronized (serverNetworksLock) {
                    Logger.debug("Attempt to remove ServerNetwork");
                    while (serverNetworksBusy) {
                        try {
                            serverNetworksLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    serverNetworks.remove(network);
                    Logger.debug("ServerNetwork removed");
                }
            }).start();
        }
    }

    private void forEachServerNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            serverNetworksBusy = true;
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
            serverNetworksBusy = false;
            serverNetworksLock.notifyAll();
        }
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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            if (arg instanceof List /*&& getUuid() != null*/) {
                Logger.debug("Updating waiting players");
                forEachServerNetwork(ServerNetwork::updateWaitingPlayers);
            }
        } else if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith(NotificationsMessages.WAITING_ROOM)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("WR Timer tick (from update): " + tick);
                forEachServerNetwork(network -> network.updateTimerTick(Methods.WR_TIMER_TICK, tick));
            }
        }
    }
}
