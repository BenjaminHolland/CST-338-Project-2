package app.csumb2017.cst338.student4338.project2.library.activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class CreateHoldActivity extends AppCompatActivity {
    LibraryDataHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hold);
        db=new LibraryDataHelper(this);
        CursorAdapter adapter=new CursorAdapter(this,db.getAvailableBooks(),false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.fragment_book,parent,false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView)view.findViewById(R.id.title_view)).setText(cursor.getString(1));

            }
        };
        ((ListView)findViewById(R.id.book_list)).setAdapter(adapter);
    }
}
