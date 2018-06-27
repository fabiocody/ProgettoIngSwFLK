package it.polimi.ingsw.server;

import java.util.*;
import java.util.function.Consumer;


class BaseController {

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

        serverNetworks.clear();
    }

    void forEachServerNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
        }
    }

}
