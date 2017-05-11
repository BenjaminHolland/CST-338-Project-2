package app.csumb2017.cst338.student4338.project2.library;

/**
 * Created by Ben on 5/11/2017.
 */

public class CreateUserInvalidUsername extends RuntimeException {
    public CreateUserInvalidUsername() {
        super("Invalid Username");
    }
}
