package app.csumb2017.cst338.student4338.project2.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ben on 5/10/2017.
 */

public class LibraryDataHelper extends SQLiteOpenHelper {
    private final static String dbFileName="Library.db";
    private final static int dbVersion=1;
    public LibraryDataHelper(Context context){
        super(context, dbFileName, null,dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LibraryDataContract.Books.CreateTable);
        db.execSQL(LibraryDataContract.Users.CreateTable);
        db.execSQL(LibraryDataContract.Holds.CreateTable);
        for(ContentValues values:LibraryDataContract.Books.getInitialData()){
            db.insert(LibraryDataContract.Books.TableName,null,values);
        }
        for(ContentValues values:LibraryDataContract.Users.getInitialData()){
            db.insert(LibraryDataContract.Users.TableName,null,values);
        }
        for(ContentValues values:LibraryDataContract.Holds.getInitialData()){
            db.insert(LibraryDataContract.Holds.TableName,null,values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LibraryDataContract.Holds.DropTable);
        db.execSQL(LibraryDataContract.Books.DropTable);
        db.execSQL(LibraryDataContract.Users.DropTable);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LibraryDataContract.Holds.DropTable);
        db.execSQL(LibraryDataContract.Books.DropTable);
        db.execSQL(LibraryDataContract.Users.DropTable);
        onCreate(db);
    }
}
