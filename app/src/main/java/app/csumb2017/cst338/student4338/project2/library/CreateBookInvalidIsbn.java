package app.csumb2017.cst338.student4338.project2.library;

/**
 * Created by Ben on 5/11/2017.
 */

public class CreateBookInvalidIsbn extends RuntimeException{
    public CreateBookInvalidIsbn() {
        super("Invalid ISBN.");
    }
}
