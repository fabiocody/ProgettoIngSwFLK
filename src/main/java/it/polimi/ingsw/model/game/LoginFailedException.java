package it.polimi.ingsw.model.game;

/**
 * This exception is thrown when the login fails, because the nickname supplied is already being used.
 *
 * @author Team
 */
public class LoginFailedException extends Exception {

    public LoginFailedException(String message) {
        super(message);
    }

}
