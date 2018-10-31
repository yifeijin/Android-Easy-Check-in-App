package example.com.shepherd.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import example.com.shepherd.data.EventContract.*;
import example.com.shepherd.data.AccountContract.*;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                EventEntry.COLUMN_DESCRIPTION + " TEXT, " +
                EventEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                EventEntry.COLUMN_START_TIME + " INTEGER NOT NULL" +
                "); ";

        final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
                AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AccountEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                AccountEntry.COLUMN_PASSWORD + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
