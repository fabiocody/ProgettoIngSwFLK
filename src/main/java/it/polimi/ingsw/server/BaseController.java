package it.polimi.ingsw.server;

import java.util.*;
import java.util.function.Consumer;


/**
 * the base class for the controllers
 */
abstract class BaseController implements Observer {

    private List<ServerNetwork> serverNetworks;
    private final Object serverNetworksLock;

    BaseController() {
        serverNetworks = new Vector<>();
        serverNetworksLock = new Object();
    }

    /**
     * This method starts a thread to add a network to the list of networks
     * @param network the network
     */
    void addNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.add(network);
                network.addObserver(this);
            }
        }).start();
    }

    /**
     * This method starts a thread to remove a network from the list of networks
     * @param network the network
     */
    void removeNetwork(ServerNetwork network) {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.remove(network);
            }
        }).start();
    }

    /**
     * This method starts a thread clear all the networks from the list of networks
     */
    void closeNetworks() {
        new Thread(() -> {
            synchronized (serverNetworksLock) {
                serverNetworks.clear();
            }
        }).start();
    }

    /**
     * This method executes an action on all the networks in the list of networks
     * @param action the specified action
     */
    void forEachNetwork(Consumer<? super ServerNetwork> action) {
        synchronized (serverNetworksLock) {
            for (ServerNetwork serverNetwork : serverNetworks) {
                action.accept(serverNetwork);
            }
            serverNetworksLock.notifyAll();
        }
    }

}
