package it.polimi.ingsw.server;

public class NicknameAlreadyUsedInGameException extends Exception {

    private final Game game;

    public NicknameAlreadyUsedInGameException(String message, Game game) {
        super(message);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
