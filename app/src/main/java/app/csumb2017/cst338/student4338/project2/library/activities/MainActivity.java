package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up the database connection.
        SQLiteOpenHelper helper= LibraryDataHelper.getInstance(this);
        findViewById(R.id.create_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createUserIntent=new Intent(MainActivity.this,CreateUserActivity.class);
                startActivityForResult(createUserIntent,0);
            }
        });
        findViewById(R.id.create_hold_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createHoldIntent=new Intent(MainActivity.this,LoginActivity.class);
                createHoldIntent.putExtra(getString(R.string.LOGIN_ADMIT_TYPE),getString(R.string.LOGIN_ADMIT_ANY));
                createHoldIntent.putExtra(getString(R.string.LOGIN_ACTION_TYPE),getString(R.string.LOGIN_ACTION_CREATE_HOLD));
                startActivityForResult(createHoldIntent,0);
            }
        });
        findViewById(R.id.destroy_hold_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent destroyHoldIntent=new Intent(MainActivity.this,LoginActivity.class);
                destroyHoldIntent.putExtra(getString(R.string.LOGIN_ADMIT_TYPE),getString(R.string.LOGIN_ADMIT_ANY));
                destroyHoldIntent.putExtra(getString(R.string.LOGIN_ACTION_TYPE),getString(R.string.LOGIN_ACTION_DESTROY_HOLD));
                startActivityForResult(destroyHoldIntent,0);
            }
        });
        findViewById(R.id.manage_system_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageSystemIntent=new Intent(MainActivity.this,LoginActivity.class);
                manageSystemIntent.putExtra(getString(R.string.LOGIN_ADMIT_TYPE),getString(R.string.LOGIN_ADMIT_ADMIN));
                manageSystemIntent.putExtra(getString(R.string.LOGIN_ACTION_TYPE),getString(R.string.LOGIN_ACTION_MANAGE_SYSTEM));
                startActivityForResult(manageSystemIntent,0);
            }
        });
    }
}
