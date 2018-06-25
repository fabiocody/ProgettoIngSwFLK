package it.polimi.ingsw.server;

import java.util.*;
import java.util.function.Consumer;

class BaseController {

    private List<ServerNetwork> serverNetworks;
    private final Object serverNetworksLock;
    private boolean serverNetworksBusy;

    BaseController() {
        serverNetworks = new Vector<>();
        serverNetworksLock = new Object();
        serverNetworksBusy = false;
    }

    void addServerNetwork(ServerNetwork network) {
        synchronized (serverNetworksLock) {
            new Thread(() -> {
                synchronized (serverNetworksLock) {
                    while (serverNetworksBusy) {
                        try {
                            serverNetworksLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    serverNetworks.add(network);
                }
            }).start();
        }
    }

    void removeServerNetwork(ServerNetwork network) {
        synchronized (serverNetworksLock) {
            new Thread(() -> {
                synchronized (serverNetworksLock) {
                    while (serverNetworksBusy) {
                        try {
                            serverNetworksLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    serverNetworks.remove(network);
                }
            }).start();
        }
    }

    void forEachServerNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            serverNetworksBusy = true;
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
            serverNetworksBusy = false;
            serverNetworksLock.notifyAll();
        }
    }

}
