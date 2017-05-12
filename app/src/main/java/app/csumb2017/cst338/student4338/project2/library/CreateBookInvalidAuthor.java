package app.csumb2017.cst338.student4338.project2.library;

/**
 * Created by Ben on 5/11/2017.
 */

public class CreateBookInvalidAuthor extends RuntimeException {
    public CreateBookInvalidAuthor() {
        super("Invalid author.");
    }
}
