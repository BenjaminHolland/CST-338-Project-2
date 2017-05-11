package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class ViewLogsActivity extends AppCompatActivity {
    LibraryDataHelper db;

    void onLeaving(){
        AlertDialog.Builder bldr=new AlertDialog.Builder(ViewLogsActivity.this);
        bldr.setMessage(R.string.CREATE_BOOK_PROMPT);
        bldr.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent createBookIntent=new Intent(getApplicationContext(),CreateBookActivity.class);
                createBookIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(createBookIntent);

                finish();
            }
        });
        bldr.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        bldr.setCancelable(false);
        bldr.show();
    }

    @Override
    public void onBackPressed() {
        onLeaving();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);
        int user=getIntent().getIntExtra("USER",-1);

        db=LibraryDataHelper.getInstance(this);
        db.log("AdminLogin|Success|\"Users.Id="+String.valueOf(user)+"\"");
        CursorAdapter logAdapter=new CursorAdapter(this,db.getLogs(),false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.fragment_log_entry,parent,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView)view.findViewById(R.id.when_view)).setText(cursor.getString(1));
                ((TextView)view.findViewById(R.id.message_view)).setText(cursor.getString(2));
            }
        };
        ((ListView)findViewById(R.id.log_list)).setAdapter(logAdapter);
        (findViewById(R.id.confirm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeaving();
            }
        });

    }
}
