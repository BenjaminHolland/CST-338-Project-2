package app.csumb2017.cst338.student4338.project2.library;

/**
 * Created by Ben on 5/11/2017.
 */

public class CreateUserDuplicateException extends RuntimeException {
    public CreateUserDuplicateException() {
        super("Duplicate username.");
    }
}
