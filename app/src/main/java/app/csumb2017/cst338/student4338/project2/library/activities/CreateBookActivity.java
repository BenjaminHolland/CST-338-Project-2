package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import app.csumb2017.cst338.student4338.project2.library.CreateBookInvalidAuthor;
import app.csumb2017.cst338.student4338.project2.library.CreateBookInvalidFee;
import app.csumb2017.cst338.student4338.project2.library.CreateBookInvalidIsbn;
import app.csumb2017.cst338.student4338.project2.library.CreateBookInvalidTitle;
import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class CreateBookActivity extends AppCompatActivity {
    private LibraryDataHelper db;

    private final int errorCounterMax=1;
    private int errorCounter=0;
    private void onErrorDialogComplete(){
        if(errorCounter<errorCounterMax){
            errorCounter++;
            Log.v("BookCreate",String.valueOf(errorCounterMax-errorCounter+1)+" attempts remaining.");
        }else {
            Log.v("BookCreate","Creation attempts exausted.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    private boolean isValidTitle(String title){
        return !((title==null)||title.isEmpty());
    }
    private boolean isValidAuthor(String author){
        return !((author==null)||author.isEmpty());
    }
    private boolean isValidIsbn(String isbn){
        return !((isbn==null)||isbn.isEmpty());
    }
    private boolean isValidFee(String fee){
        try{
            Double.parseDouble(fee);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
    private void onError(Throwable error){
        Log.e("BookCreateAttempt","Failure",error);
        db.log("BookCreateAttempt|Failure|\""+error.getMessage()+"\"");
        AlertDialog.Builder bldr=new AlertDialog.Builder(this);
        bldr.setTitle("Error");
        bldr.setMessage(error.getMessage());
        bldr.setNeutralButton("OK",null);
        bldr.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onErrorDialogComplete();
            }
        });
        bldr.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);
        db=LibraryDataHelper.getInstance(getApplicationContext());
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=((EditText)findViewById(R.id.title_input)).getText().toString();
                final String author=((EditText)findViewById(R.id.author_input)).getText().toString();
                final String isbn=((EditText)findViewById(R.id.isbn_input)).getText().toString();
                final String feeStr=((EditText)findViewById(R.id.fee_input)).getText().toString();

                try {
                    if(!isValidTitle(title)){
                        throw new CreateBookInvalidTitle();
                    }
                    if(!isValidAuthor(author)){
                        throw new CreateBookInvalidAuthor();
                    }
                    if(!isValidIsbn(isbn)){
                        throw new CreateBookInvalidIsbn();
                    }
                    if(!isValidFee(feeStr)){
                        throw new CreateBookInvalidFee();
                    }
                    final double fee=Double.parseDouble(((EditText)findViewById(R.id.fee_input)).getText().toString());
                    db.createBook(title, author, isbn, fee);
                    db.log("CreateBook|Success|\"Created '"+title+"'\"");
                    setResult(RESULT_OK);
                    finish();
                }catch(Exception ex){

                    onError(ex);
                }

            }
        });
    }
}
