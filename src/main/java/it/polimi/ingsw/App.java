package it.polimi.ingsw;

import it.polimi.ingsw.server.SagradaServer;


public class App {

    public static void main(String[] args) {
        SagradaServer.getInstance().startSocketServer();
    }

}
