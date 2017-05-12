package app.csumb2017.cst338.student4338.project2.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;

import app.csumb2017.cst338.student4338.project2.library.LoginIncorrectPasswordException;
import app.csumb2017.cst338.student4338.project2.library.LoginNotAuthorizedException;
import app.csumb2017.cst338.student4338.project2.library.LoginUserNotFoundException;

/**
 * Created by Ben on 5/10/2017.
 */

public class LibraryDataHelper extends SQLiteOpenHelper {
    private final static Object lazy_sync=new Object();
    private static LibraryDataHelper instance;
    public static LibraryDataHelper getInstance(Context context){
        synchronized (lazy_sync){
            if(instance==null){
                instance=new LibraryDataHelper(context.getApplicationContext());
            }
        }
        return instance;
    }

    private final static String dbFileName = "Library.db";
    private final static int dbVersion = 1;

    private LibraryDataHelper(Context context) {
        super(context, dbFileName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LibraryDataContract.Books.CreateTable);
        db.execSQL(LibraryDataContract.Users.CreateTable);
        db.execSQL(LibraryDataContract.Holds.CreateTable);
        db.execSQL(LibraryDataContract.Logs.CreateTable);
        for (ContentValues values : LibraryDataContract.Books.getInitialData()) {
            db.insertOrThrow(LibraryDataContract.Books.TableName, null, values);
        }
        for (ContentValues values : LibraryDataContract.Users.getInitialData()) {
            db.insert(LibraryDataContract.Users.TableName, null, values);
        }
        for (ContentValues values : LibraryDataContract.Holds.getInitialData()) {
            db.insert(LibraryDataContract.Holds.TableName, null, values);
        }
        for (ContentValues values : LibraryDataContract.Logs.getIntialData()) {
            db.insert(LibraryDataContract.Logs.TableName, null, values);
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

    public void log(String message) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        getWritableDatabase().insert(LibraryDataContract.Logs.TableName, null, LibraryDataContract.Logs.buildEntry(now, message));
    }

    public Cursor getLogs() {
        return getReadableDatabase().query(
                LibraryDataContract.Logs.TableName,
                null,
                null,
                null,
                null,
                null,
                LibraryDataContract.Logs.Columns.When);
    }
    public Cursor getHoldsForUser(int user){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+LibraryDataContract.Holds.TableName+
                        " JOIN "+LibraryDataContract.Books.TableName+
                        " ON "+LibraryDataContract.Holds.TableName+"."+LibraryDataContract.Holds.Columns.TargetBook+
                        " = "+LibraryDataContract.Books.TableName+"."+LibraryDataContract.Books.Columns._ID+
                        " WHERE "+LibraryDataContract.Holds.Columns.TargetUser+"=?"+
                        " AND "+LibraryDataContract.Holds.Columns.IsActive+"=1",new String[]{String.valueOf(user)});
    }
    public String getBookTitle(int book){
        Cursor c=getReadableDatabase().rawQuery("SELECT Title FROM Books WHERE Books._id=?",new String[]{String.valueOf(book)});
        if(c.getCount()==0){
            return "";
        }else{
            try{
                c.moveToFirst();
                return c.getString(0);
            }catch (Exception ex){
                Log.e("???","Error",ex);
                return "";
            }
        }

    }
    public String getUsername(int user){
        Cursor c=getReadableDatabase().rawQuery("SELECT Username FROM Users WHERE Users._id=?",new String[]{String.valueOf(user)});
        if(c.getCount()==0){
            return "";
        }try{
            c.moveToFirst();
            return c.getString(0);
        }catch (Exception ex){
            Log.e("???","Error",ex);
            return "";
        }
    }
    public long createHold(int user,int book,double totalFee,long checkout,long checkin,long creation){

        return getWritableDatabase().insertOrThrow(LibraryDataContract.Holds.TableName,null,
                LibraryDataContract.Holds.buildEntry(
                        user,
                        book,
                        new Timestamp(checkout),
                        new Timestamp(checkin),
                        new Timestamp(creation),
                        totalFee,
                        true)
        );
    }
    public void destroyHold(int hold){
        ContentValues disableHold=new ContentValues();
        disableHold.put(LibraryDataContract.Holds.Columns.IsActive,0);
        getWritableDatabase().update(
                LibraryDataContract.Holds.TableName,
                disableHold,
                LibraryDataContract.Holds.Columns._ID+"=?",
                new String[]{String.valueOf(hold)}
        );
    }
    public void createUser(String username,String password){
        getWritableDatabase().insertOrThrow(LibraryDataContract.Users.TableName,null,LibraryDataContract.Users.buildEntry(username,password,false));
    }
    public Cursor getAvailableBooks() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + LibraryDataContract.Books.TableName +
                        " WHERE " + LibraryDataContract.Books.Columns._ID +
                        " NOT IN (SELECT "
                        + LibraryDataContract.Holds.Columns.TargetBook +
                        " FROM " + LibraryDataContract.Holds.TableName +
                        " WHERE " + LibraryDataContract.Holds.Columns.IsActive + "=1)",
                null);
    }

    public void createBook(String title,String author, String isbn,double fee){
        getWritableDatabase().insertOrThrow(
                LibraryDataContract.Books.TableName,
                null,
                LibraryDataContract.Books.buildEntry(title,author,isbn,fee));

    }
    public int login(String username, String password, boolean requireAdmin) {
        Cursor result = getReadableDatabase().query(
                LibraryDataContract.Users.TableName,
                new String[]{
                        LibraryDataContract.Users.Columns._ID,
                        LibraryDataContract.Users.Columns.Password,
                        LibraryDataContract.Users.Columns.IsAdmin
                },
                LibraryDataContract.Users.Columns.Username + "=?",
                new String[]{username},
                null,
                null,
                null);

        if (result.getCount() <= 0) {
            throw new LoginUserNotFoundException();
        }
        result.moveToFirst();
        if (!result.getString(1).equals(password)) {
            throw new LoginIncorrectPasswordException();
        }
        if ((result.getInt(2) == 0) && requireAdmin) {
            throw new LoginNotAuthorizedException();
        }
        return result.getInt(0);
    }


}
