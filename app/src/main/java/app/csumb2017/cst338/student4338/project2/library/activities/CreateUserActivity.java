package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.csumb2017.cst338.student4338.project2.library.CreateUserInvalidPassword;
import app.csumb2017.cst338.student4338.project2.library.CreateUserInvalidUsername;
import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class CreateUserActivity extends AppCompatActivity {
    private final static List<Character> SPECIAL_CHARACTERS= Collections.unmodifiableList(new ArrayList<Character>(){{
        add('!');
        add('@');
        add('#');
        add('$');
    }});
    private final static int ALPHA_IDX=0;
    private final static int NUMER_IDX=1;
    private final static int SPECI_IDX=2;
    private static boolean isCredentialValid(String credential){
        int[] counts=new int[3];
        char[] arr=credential.toCharArray();
        for(char c:arr){
            if(Character.isLetter(c)){
                counts[ALPHA_IDX]++;
            }else if(Character.isDigit(c)){
                counts[NUMER_IDX]++;
            }else if(SPECIAL_CHARACTERS.contains(c)){
                counts[SPECI_IDX]++;
            }
        }
        return counts[ALPHA_IDX]>=3&&counts[NUMER_IDX]>=1&&counts[SPECI_IDX]>=1;
    }
    private static boolean isPasswordValid(String password){
        return isCredentialValid(password);
    }
    private static boolean isUsernameValid(String username){
        return isCredentialValid(username);
    }
    private LibraryDataHelper db;

    private final int errorCounterMax=1;
    private int errorCounter=0;
    private void onErrorDialogComplete(){
        if(errorCounter<errorCounterMax){
            errorCounter++;
            Log.v("UserCreate",String.valueOf(errorCounterMax-errorCounter+1)+" attempts remaining.");
        }else {
            Log.v("UserCreate","Creation attempts exausted.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    private void onError(Throwable error){
        Log.e("UserCreateAttempt","Failure",error);
        db.log("UserCreateAttempt|Failure|\""+error.getMessage()+"\"");
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
        setContentView(R.layout.activity_create_user);
        db=new LibraryDataHelper(this);
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=((EditText)findViewById(R.id.username_input)).getText().toString();
                String password=((EditText)findViewById(R.id.password_input)).getText().toString();
                try {
                    if (!isUsernameValid(username)){
                        throw new CreateUserInvalidUsername();
                    }
                    if(!isPasswordValid(password)){
                        throw new CreateUserInvalidPassword();
                    }
                    db.createUser(username,password);
                    db.log("UserCreateAttempt|Success|\"Created "+username+"\"");
                    CreateUserActivity.this.setResult(RESULT_OK);
                    CreateUserActivity.this.finish();
                }catch(Exception ex){
                    onError(ex);
                }
            }
        });
    }
}
