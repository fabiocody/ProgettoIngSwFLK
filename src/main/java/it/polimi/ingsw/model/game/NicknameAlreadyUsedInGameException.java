package it.polimi.ingsw.model.game;

import it.polimi.ingsw.server.GameController;


public class NicknameAlreadyUsedInGameException extends Exception {

    private final Game game;
    private GameController controller;

    NicknameAlreadyUsedInGameException(String message, Game game) {
        super(message);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public GameController getController() {
        return controller;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }
}
