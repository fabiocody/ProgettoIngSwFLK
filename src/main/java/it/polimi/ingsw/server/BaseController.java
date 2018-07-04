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

    void addNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.add(network);
                network.addObserver(this);
            }
        }).start();
    }

    void removeNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.remove(network);
            }
        }).start();
    }

    void closeNetworks() {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.clear();
            }
        }).start();
    }

    void forEachNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
            serverNetworksLock.notifyAll();
        }
    }

}
