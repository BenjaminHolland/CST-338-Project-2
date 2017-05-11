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
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.DecimalFormat;


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
        db=new LibraryDataHelper(this);
        CursorAdapter adapter=new CursorAdapter(this,db.getHoldsForUser(id),false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.fragment_hold,parent,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                final DecimalFormat feeFormat=new DecimalFormat("\u00A4#0.00");
                final int holdId=cursor.getInt(0);
                final Timestamp checkout= Timestamp.valueOf(cursor.getString(3));
                final Timestamp checkin=Timestamp.valueOf(cursor.getString(4));
                final double fee=cursor.getDouble(6);
                final String title=cursor.getString(9);
                ((TextView)view.findViewById(R.id.title_view)).setText(title);
                ((TextView)view.findViewById(R.id.fee_view)).setText(feeFormat.format(fee));
                ((TextView)view.findViewById(R.id.checkout_view)).setText(checkout.toString());
                ((TextView)view.findViewById(R.id.checkin_view)).setText(checkin.toString());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            db.destroyHold(holdId);
                            db.log("DestroyHold|Success|\"Hold="+String.valueOf(holdId)+"\"");
                            DestroyHoldActivity.this.setResult(RESULT_OK);

                        }catch(Exception ex){
                            db.log("DestroyHold|Failure|"+ex.getMessage());
                            DestroyHoldActivity.this.setResult(RESULT_CANCELED);
                        }
                        DestroyHoldActivity.this.finish();
                    }
                });
            }
        };
        ((ListView)findViewById(R.id.hold_list)).setAdapter(adapter);

    }
}
