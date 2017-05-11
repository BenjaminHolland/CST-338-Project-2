package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import app.csumb2017.cst338.student4338.project2.library.R;

public class CreateUserActivity extends AppCompatActivity {
    private final int errorCounterMax=1;
    private int errorCounter=0;
    private void onErrorDialogComplete(){
        if(errorCounter<errorCounterMax){
            errorCounter++;
            Log.v("LOGIN",String.valueOf(errorCounterMax-errorCounter+1)+" attempts remaining.");
        }else {
            Log.v("LOGIN","Login attempts exausted.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    private void onError(Throwable error){
        Log.e("LOGIN","Failure",error);
        db.log("LoginAttempt|Failure|\""+error.getMessage()+"\"");
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
    }
}
