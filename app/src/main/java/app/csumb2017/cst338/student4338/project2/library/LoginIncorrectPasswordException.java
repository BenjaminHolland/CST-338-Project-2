package app.csumb2017.cst338.student4338.project2.library;

/**
 * Created by Ben on 5/10/2017.
 */

public class LoginIncorrectPasswordException extends RuntimeException {
    public LoginIncorrectPasswordException() {
        super("Incorrect password.");
    }
}
