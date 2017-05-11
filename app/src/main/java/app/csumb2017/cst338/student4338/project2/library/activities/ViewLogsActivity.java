package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.Context;
import android.database.Cursor;
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
    }
}
