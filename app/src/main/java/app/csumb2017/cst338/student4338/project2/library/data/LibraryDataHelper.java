package app.csumb2017.cst338.student4338.project2.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;
import java.sql.Timestamp;

import app.csumb2017.cst338.student4338.project2.library.LoginIncorrectPassword;
import app.csumb2017.cst338.student4338.project2.library.LoginNotAuthorizedException;
import app.csumb2017.cst338.student4338.project2.library.LoginUserNotFoundException;

/**
 * Created by Ben on 5/10/2017.
 */

public class LibraryDataHelper extends SQLiteOpenHelper {
    private final static String dbFileName = "Library.db";
    private final static int dbVersion = 1;

    public LibraryDataHelper(Context context) {
        super(context, dbFileName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LibraryDataContract.Books.CreateTable);
        db.execSQL(LibraryDataContract.Users.CreateTable);
        db.execSQL(LibraryDataContract.Holds.CreateTable);
        db.execSQL(LibraryDataContract.Logs.CreateTable);
        for (ContentValues values : LibraryDataContract.Books.getInitialData()) {
            db.insert(LibraryDataContract.Books.TableName, null, values);
        }
        for (ContentValues values : LibraryDataContract.Users.getInitialData()) {
            db.insert(LibraryDataContract.Users.TableName, null, values);
        }
        for (ContentValues values : LibraryDataContract.Holds.getInitialData()) {
            db.insert(LibraryDataContract.Holds.TableName, null, values);
        }
        for(ContentValues values:LibraryDataContract.Logs.getIntialData()){
            db.insert(LibraryDataContract.Logs.TableName,null,values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LibraryDataContract.Holds.DropTable);
        db.execSQL(LibraryDataContract.Books.DropTable);
        db.execSQL(LibraryDataContract.Users.DropTable);
        db.execSQL(LibraryDataContract.Logs.DropTable);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LibraryDataContract.Holds.DropTable);
        db.execSQL(LibraryDataContract.Books.DropTable);
        db.execSQL(LibraryDataContract.Users.DropTable);
        db.execSQL(LibraryDataContract.Logs.DropTable);
        onCreate(db);
    }
    public void log(String message){
        Timestamp now=new Timestamp(System.currentTimeMillis());
        getWritableDatabase().insert(LibraryDataContract.Logs.TableName,null,LibraryDataContract.Logs.buildEntry(now,message));
    }
    public Cursor getLogs(){
        return getReadableDatabase().query(
                LibraryDataContract.Logs.TableName,
                null,
                null,
                null,
                null,
                null,
                LibraryDataContract.Logs.Columns.When);
    }
    public int login(String username, String password, boolean requireAdmin) {
        Cursor result=getReadableDatabase().query(
                LibraryDataContract.Users.TableName,
                new String[]{
                        LibraryDataContract.Users.Columns._ID,
                        LibraryDataContract.Users.Columns.Password,
                        LibraryDataContract.Users.Columns.IsAdmin
                },
                LibraryDataContract.Users.Columns.Username+"=?",
                new String[]{username},
                null,
                null,
                null);

        if(result.getCount()<=0){
            throw new LoginUserNotFoundException();
        }
        result.moveToFirst();
        if(!result.getString(1).equals(password)){
            throw new LoginIncorrectPassword();
        }
        if((result.getInt(2)==0)&&requireAdmin){
            throw new LoginNotAuthorizedException();
        }
        return result.getInt(0);
    }


}
