package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class LoginActivity extends AppCompatActivity {
    private String loginAdmit;
    private String loginAction;
    private LibraryDataHelper db;
    private void ensureTargets(){
        boolean isAcceptValid=loginAdmit.equals(getString(R.string.LOGIN_ADMIT_ANY))||
                loginAdmit.equals((getString(R.string.LOGIN_ADMIT_ADMIN)));
        boolean isActionValid=loginAction.equals(getString(R.string.LOGIN_ACTION_CREATE_HOLD))||
                loginAction.equals(getString(R.string.LOGIN_ACTION_DESTROY_HOLD))||
                loginAction.equals(getString(R.string.LOGIN_ACTION_MANAGE_SYSTEM));
        boolean isValid=isAcceptValid&&isActionValid;
        if(!isValid){
            Log.wtf("LOGIN","Invalid targets.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    private void launchManageSystem(int user){
        Log.v("LOGIN","Launching System Manager.");
        Intent manageSystemIntent=new Intent(LoginActivity.this,ViewLogsActivity.class);
        manageSystemIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        manageSystemIntent.putExtra("USER",user);
        startActivity(manageSystemIntent);
        finish();
    }
    private void launchCreateHold(int user){
        Log.v("LOGIN","Launching Hold Creator.");
        Intent createHoldIntent=new Intent(LoginActivity.this,CreateHoldActivity.class);
        createHoldIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        createHoldIntent.putExtra("USER",user);
        startActivity(createHoldIntent);
        finish();
    }
    private void launchDestroyHold(int user){
        Log.v("LOGIN","Launching Hold Destroyer.");
        finish();
    }
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
        setContentView(R.layout.activity_login);
        Intent intent=getIntent();
        loginAdmit=intent.getStringExtra(getString(R.string.LOGIN_ADMIT_TYPE));
        loginAction=intent.getStringExtra(getString(R.string.LOGIN_ACTION_TYPE));
        ensureTargets();
        db=new LibraryDataHelper(this);
        final EditText usernameInput=(EditText)findViewById(R.id.username_input);
        final EditText passwordInput=(EditText)findViewById(R.id.password_input);
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameInput.getText().toString();
                String password=passwordInput.getText().toString();
                try{
                    boolean requireAdmin=loginAdmit.equals(getString(R.string.LOGIN_ADMIT_ADMIN));
                    int user=db.login(username,password,requireAdmin);
                    db.log("LoginAttempt|Success|\"Users.Id="+String.valueOf(user)+"\"");
                    if(loginAction.equals(getString(R.string.LOGIN_ACTION_CREATE_HOLD))){
                        launchCreateHold(user);
                    }else if(loginAction.equals(getString(R.string.LOGIN_ACTION_DESTROY_HOLD))){
                        launchDestroyHold(user);
                    }else {
                        launchManageSystem(user);
                    }
                }catch(Exception ex){
                    onError(ex);
                }
            }
        });
    }
}
