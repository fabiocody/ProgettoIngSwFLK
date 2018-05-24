package it.polimi.ingsw.client;


public interface ClientNetwork {

    void setup();
    void teardown();

    void sendAddPlayer(String nickname);

}
