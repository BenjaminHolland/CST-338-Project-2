package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.StringJoiner;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class DestroyHoldActivity extends AppCompatActivity {
    private int id;
    private LibraryDataHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destroy_hold);
        id=getIntent().getIntExtra("USER",-1);
        CursorAdapter adapter=new CursorAdapter(this,db.getHoldsForUser(id),false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.fragment_hold,parent,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                StringBuilder bldr=new StringBuilder();
                String[] columnNames=cursor.getColumnNames();
                for(int i=0;i<columnNames.length-1;i++){
                    bldr.append(columnNames[i]);
                    bldr.append(",");
                }
                bldr.append(columnNames[columnNames.length-1]);
                Log.i("DestroyHold", bldr.toString());
            }
        };
        ((ListView)findViewById(R.id.hold_list)).setAdapter(adapter);

    }
}
