package app.csumb2017.cst338.student4338.project2.library.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.csumb2017.cst338.student4338.project2.library.CreateHoldInvalidTimespanException;
import app.csumb2017.cst338.student4338.project2.library.R;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataContract;
import app.csumb2017.cst338.student4338.project2.library.data.LibraryDataHelper;

public class CreateHoldActivity extends AppCompatActivity {
    LibraryDataHelper db;
    private final int errorCounterMax = 1;
    private int errorCounter = 0;

    private void onErrorDialogComplete() {
        if (errorCounter < errorCounterMax) {
            errorCounter++;
            Log.v("UserCreate", String.valueOf(errorCounterMax - errorCounter + 1) + " attempts remaining.");
        } else {
            Log.v("UserCreate", "Creation attempts exausted.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void onError(Throwable error) {
        Log.e("UserCreateAttempt", "Failure", error);
        db.log("UserCreateAttempt|Failure|\"" + error.getMessage() + "\"");
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setTitle("Error");
        bldr.setMessage(error.getMessage());
        bldr.setNeutralButton("OK", null);
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
        setContentView(R.layout.activity_create_hold);
        final int user = getIntent().getIntExtra("USER", -1);
        db = LibraryDataHelper.getInstance(this);
        CursorAdapter adapter = new CursorAdapter(this, db.getAvailableBooks(), false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return LayoutInflater.from(context).inflate(R.layout.fragment_book, parent, false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                //It feels very strange capturing the context of a method in a class. Maybe this
                //is just a java thing, but it seems kind of inefficient.
                final DecimalFormat feeFormat = new DecimalFormat("\u00A4#0.00");
                final int id = cursor.getInt(0);
                final String title = cursor.getString(1);
                final String author = cursor.getString(2);
                final String isbn = cursor.getString(3);
                final double fee = cursor.getDouble(4);

                ((TextView) view.findViewById(R.id.title_view)).setText(title);
                ((TextView) view.findViewById(R.id.author_view)).setText(author);
                ((TextView) view.findViewById(R.id.fee_view)).setText(feeFormat.format(fee));
                ((TextView) view.findViewById(R.id.isbn_view)).setText(isbn);

                //This is disgusting. We should have some way of making this less terrible.
                view.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {

                        final Calendar checkout = Calendar.getInstance();
                        final Calendar checkin = Calendar.getInstance();
                        final Calendar now = Calendar.getInstance();
                        now.setTime(new Date());
                        DatePickerDialog checkoutDatePicker = new DatePickerDialog(CreateHoldActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                checkout.set(Calendar.YEAR, year);
                                checkout.set(Calendar.MONTH, month);
                                checkout.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                TimePickerDialog checkoutTimePicker = new TimePickerDialog(CreateHoldActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        checkout.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        checkout.set(Calendar.MINUTE, minute);
                                        DatePickerDialog checkinDatePicker = new DatePickerDialog(CreateHoldActivity.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                        checkin.set(Calendar.YEAR, year);
                                                        checkin.set(Calendar.MONTH, month);
                                                        checkin.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                        TimePickerDialog checkinTimePicker = new TimePickerDialog(CreateHoldActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                checkin.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                checkin.set(Calendar.MINUTE, minute);
                                                                try {
                                                                    long dt = checkin.getTimeInMillis() - checkout.getTimeInMillis();
                                                                    if (dt <= 0) {
                                                                        throw new CreateHoldInvalidTimespanException();
                                                                    }

                                                                    dt /= 1000;
                                                                    dt /= 60;
                                                                    dt /= 60;
                                                                    final double feeTotal = fee * dt;
                                                                    final double days=dt/24.0;

                                                                    final DecimalFormat feeFormat=new DecimalFormat("\u00A4#0.00");
                                                                    AlertDialog.Builder bldr = new AlertDialog.Builder(CreateHoldActivity.this);
                                                                    try {
                                                                        if(days>7){
                                                                            throw new CreateHoldInvalidTimespanException();
                                                                        }
                                                                        final Calendar creation = Calendar.getInstance();
                                                                        final DateFormat format=DateFormat.getDateTimeInstance();
                                                                        creation.setTime(new Date());
                                                                        long newHoldId=db.createHold(user, id, feeTotal, checkout.getTimeInMillis(), checkin.getTimeInMillis(), creation.getTimeInMillis());
                                                                        db.log("PlaceHold|Success|\"Book=" + String.valueOf(id) + "\"");
                                                                        AlertDialog.Builder finalResult=new AlertDialog.Builder(CreateHoldActivity.this);
                                                                        View holdView=getLayoutInflater().inflate(R.layout.fragment_hold_confirmation,null);
                                                                        Date checkinDate=new Date(checkin.getTimeInMillis());
                                                                        Date checkoutDate=new Date(checkout.getTimeInMillis());
                                                                        ((TextView)holdView.findViewById(R.id.hold_view)).setText(String.valueOf(newHoldId));
                                                                        ((TextView)holdView.findViewById(R.id.title_view)).setText(title);
                                                                        ((TextView)holdView.findViewById(R.id.checkin_view)).setText(format.format(checkinDate));
                                                                        ((TextView)holdView.findViewById(R.id.checkout_view)).setText(format.format(checkoutDate));
                                                                        ((TextView)holdView.findViewById(R.id.fee_view)).setText(feeFormat.format(feeTotal));
                                                                        ((TextView)holdView.findViewById(R.id.username_view)).setText(db.getUsername(user));
                                                                        finalResult.setView(holdView);
                                                                        finalResult.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                            @Override
                                                                            public void onDismiss(DialogInterface dialog) {
                                                                                CreateHoldActivity.this.setResult(RESULT_OK, null);
                                                                                finish();
                                                                            }
                                                                        });
                                                                        finalResult.setNeutralButton(R.string.CONFIRM,null);
                                                                        finalResult.show();
                                                                    } catch (Exception ex) {
                                                                        onError(ex);
                                                                    }
                                                                } catch (Exception ex) {
                                                                    onError(ex);
                                                                }

                                                            }
                                                        }, 12, 0, false);
                                                        checkinTimePicker.setTitle("Checkin Time");
                                                        checkinTimePicker.setCancelable(false);
                                                        checkinTimePicker.show();
                                                    }
                                                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                                        checkinDatePicker.setTitle("Checkin Date");
                                        checkinDatePicker.setCancelable(false);
                                        checkinDatePicker.show();
                                    }
                                }, 12, 0, false);
                                checkoutTimePicker.setTitle("Checkout Time");
                                checkoutTimePicker.setCancelable(false);
                                checkoutTimePicker.show();
                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        checkoutDatePicker.setTitle("Checkout Date");
                        checkoutDatePicker.setCancelable(false);
                        checkoutDatePicker.create();
                        checkoutDatePicker.show();
                    }
                });
            }
        };


        ((ListView) findViewById(R.id.book_list)).setAdapter(adapter);
    }
}
