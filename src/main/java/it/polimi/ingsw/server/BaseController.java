package it.polimi.ingsw.server;

import java.util.*;
import java.util.function.Consumer;


abstract class BaseController implements Observer {

    private List<ServerNetwork> serverNetworks;
    private final Object serverNetworksLock;

    BaseController() {
        serverNetworks = new Vector<>();
        serverNetworksLock = new Object();
    }

    void addServerNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.add(network);
                network.addObserver(this);
            }
        }).start();
    }

    void removeServerNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.remove(network);
            }
        }).start();
    }

    void closeServerNetworks() {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.clear();
            }
        }).start();
    }

    void forEachServerNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
            serverNetworksLock.notifyAll();
        }
    }

}
