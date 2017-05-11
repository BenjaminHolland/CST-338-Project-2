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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataContract;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class CreateHoldActivity extends AppCompatActivity {
    LibraryDataHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hold);
        final int user=getIntent().getIntExtra("USER",-1);
        db=new LibraryDataHelper(this);
        CursorAdapter adapter=new CursorAdapter(this,db.getAvailableBooks(),false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return LayoutInflater.from(context).inflate(R.layout.fragment_book,parent,false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView)view.findViewById(R.id.title_view)).setText(cursor.getString(1));
                view.setTag(R.string.HOLD_VIEW_TARGET_BOOK_TAG,cursor.getInt(0));
                view.setTag(R.string.HOLD_VIEW_TARGET_FEE_TAG,cursor.getFloat(4));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date now=new Date();
                        Calendar checkout=Calendar.getInstance();
                        Calendar checkin=Calendar.getInstance();
                        Calendar creation=Calendar.getInstance();
                        checkout.setTime(now);
                        checkin.setTime(now);
                        creation.setTime(now);
                        checkin.add(Calendar.DAY_OF_YEAR,10);
                        double feeTotal=(double)(float)v.getTag(R.string.HOLD_VIEW_TARGET_FEE_TAG);
                        feeTotal*=10*24;
                        db.log("PlaceHold|Success|\"Book="+String.valueOf((int)v.getTag(R.string.HOLD_VIEW_TARGET_BOOK_TAG))+"\"");
                        db.getWritableDatabase().insertOrThrow(LibraryDataContract.Holds.TableName,null,
                                LibraryDataContract.Holds.buildEntry(
                                        user,
                                        (int)v.getTag(R.string.HOLD_VIEW_TARGET_BOOK_TAG),
                                        new Timestamp(checkout.getTimeInMillis()),
                                        new Timestamp(checkin.getTimeInMillis()),
                                        new Timestamp(creation.getTimeInMillis()),
                                        feeTotal,
                                        true));
                    setResult(RESULT_OK,null);
                    CreateHoldActivity.this.finish();
                    }
                });
            }
        };
        ((ListView)findViewById(R.id.book_list)).setAdapter(adapter);
    }
}
