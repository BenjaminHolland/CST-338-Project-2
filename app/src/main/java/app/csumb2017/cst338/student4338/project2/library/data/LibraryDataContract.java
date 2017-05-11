package app.csumb2017.cst338.student4338.project2.library.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ben on 5/10/2017.
 */

public class LibraryDataContract {
    private LibraryDataContract() {
    }
    public final static class Logs {
        public static final String TableName = "Logs";

        public static final class Columns implements BaseColumns {
            public static final String When = "_When";
            public static final String Message = "Message";
        }

        public static final String CreateTable = "CREATE TABLE " + TableName + "(" +
                Columns._ID + " INTEGER PRIMARY KEY," +
                Columns.When + " TEXT NOT NULL," +
                Columns.Message + " TEXT NOT NULL" +
                ")";
        public static final String DropTable="DROP TABLE IF EXISTS "+TableName;
        public static ContentValues buildEntry(Timestamp when,String message){
            ContentValues values=new ContentValues();
            values.put(Columns.When,when.toString());
            values.put(Columns.Message,message.toString());
            return values;
        }
        public static List<ContentValues> getIntialData(){
            return Collections.unmodifiableList(new ArrayList<ContentValues>());
        }
    }
    public final static class Books {

        public static final String TableName = "Books";

        public final static class Columns implements BaseColumns {
            public static final String Title = "Title";
            public static final String Author = "Author";
            public static final String Isbn = "Isbn";
            public static final String Fee = "Fee";
        }

        public final static String CreateTable = "CREATE TABLE " + TableName + "(" +
                Columns._ID + " INTEGER PRIMARY KEY," +
                Columns.Title + " TEXT NOT NULL," +
                Columns.Author + "TEXT NOT NULL," +
                Columns.Isbn + " TEXT NOT NULL UNIQUE," +
                Columns.Fee + " REAL NOT NULL" +
                ")";
        public final static String DropTable = "DROP TABLE IF EXISTS " + TableName;
        public static ContentValues buildEntry(String title,String author,String isbn,double fee){
            ContentValues values=new ContentValues();
            values.put(Columns.Title,title);
            values.put(Columns.Author,author);
            values.put(Columns.Isbn,isbn);
            values.put(Columns.Fee,fee);
            return values;
        }
        public static List<ContentValues> getInitialData(){
            return Collections.unmodifiableList(new ArrayList<ContentValues>(){{
                add(buildEntry("Hot Java","S. Narayanan","123-ABC-101",0.05));
                add(buildEntry("Fun Java","Y. Byun","ABCDEF-09",1.00));
                add(buildEntry("Algorithms for Java","K. Alice","CDE-777-123",0.25));
            }});
        }
    }

    public final static class Users {
        public final static String TableName = "Users";

        public final static class Columns implements BaseColumns {
            public static final String Username = "Username";
            public static final String Password = "Password";
            public static final String IsAdmin = "IsAdmin";
        }

        public final static String CreateTable = "CREATE TABLE " + TableName + " (" +
                Columns._ID + " INTEGER PRIMARY KEY, " +
                Columns.Username + " TEXT NOT NULL UNIQUE, " +
                Columns.Password + " TEXT NOT NULL, " +
                Columns.IsAdmin + " INTEGER NOT NULL" +
                ")";
        public final static String DropTable = "DROP TABLE IF EXISTS " +TableName;
        public static ContentValues buildEntry(String username,String password,boolean isAdmin){
            ContentValues values=new ContentValues();
            values.put(Columns.Username,username);
            values.put(Columns.Password,password);
            values.put(Columns.IsAdmin,isAdmin);
            return values;
        }
        public static List<ContentValues> getInitialData(){
            return Collections.unmodifiableList(new ArrayList<ContentValues>(){{
                add(buildEntry("a@lice5","@csit100",false));
                add(buildEntry("$brian7","123abc##",false));
                add(buildEntry("!chris12!","CHRIS12!!",false));
                add(buildEntry("!admin2","!admin2",true));
            }});
        }
    }

    public final static class Holds {
        public final static String TableName = "Holds";

        public final static class Columns implements BaseColumns {
            public static final String TargetUser = "TargetUser";
            public static final String TargetBook = "TargetBook";
            public static final String CheckOutDate = "CheckOutDate";
            public static final String CheckInDate = "CheckInDate";
            public static final String CreationDate = "CreationDate";
            public static final String TotalFee = "TotalFee";
            public static final String IsActive = "IsActive";
        }

        public final static String CreateTable = "CREATE TABLE " + TableName + "(" +
                Columns._ID + " INTEGER PRIMARY KEY," +
                Columns.TargetUser + " INTEGER NOT NULL," +
                Columns.TargetBook + " INTEGER NOT NULL," +
                Columns.CheckOutDate + " TEXT NOT NULL," +
                Columns.CheckInDate + " TEXT NOT NULL," +
                Columns.CreationDate + " TEXT NOT NULL," +
                Columns.TotalFee + " REAL NOT NULL," +
                Columns.IsActive + " INTEGER NOT NULL," +
                "FOREIGN KEY ("+Columns.TargetUser+") REFERENCES "+Users.TableName+"("+Users.Columns._ID+")"+
                ") ";
        public final static String DropTable="DROP TABLE IF EXISTS "+TableName;
        public static ContentValues buildEntry(int targetUser, int targetBook, Timestamp checkoutDate,Timestamp checkinDate,Timestamp creationDate,double totalFee,boolean isActive){
            ContentValues values=new ContentValues();
            values.put(Columns.TargetUser,targetUser);
            values.put(Columns.TargetBook,targetBook);
            values.put(Columns.CheckOutDate,checkoutDate.toString());
            values.put(Columns.CheckInDate,checkinDate.toString());
            values.put(Columns.CreationDate,creationDate.toString());
            values.put(Columns.TotalFee,totalFee);
            values.put(Columns.IsActive,isActive);
            return values;
        }
        public static List<ContentValues> getInitialData(){
            return Collections.unmodifiableList(new ArrayList<ContentValues>());
        }
    }

}
