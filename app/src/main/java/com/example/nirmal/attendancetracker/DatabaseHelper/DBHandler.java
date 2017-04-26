package com.example.nirmal.attendancetracker.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by nirmal on 26/4/17.
 */

public class DBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Roken";
    private static final String TABLE_NAME = "UserTempDetails";
    private static final String UniqueIdNameKey = "Unique_ID";
    private static final String TimeStampKey = "TimeStamp";
    private static final String DurationKey = "Duration";
    private static final String FlagKey = "Flag";

    public DBHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CreateTableQuery = "CREATE TABLE "   + TABLE_NAME + "(" + UniqueIdNameKey + "INTEGER,"+TimeStampKey+"Timestamp DEFAULT CURRENT_TIMESTAMP,"+DurationKey+"Integer,"+FlagKey+"Integer)";
        sqLiteDatabase.execSQL(CreateTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long InsertData(DataEntryModel valuesToEnter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UniqueIdNameKey,valuesToEnter.getUniqueId());
        contentValues.put(TimeStampKey,valuesToEnter.getTimeStamp());
        contentValues.put(DurationKey,valuesToEnter.getDuration());
        contentValues.put(FlagKey,valuesToEnter.getFlag());
        long output = db.insert(DATABASE_NAME,null,contentValues);
        db.close();
        return output;
    }

    public ArrayList<DataEntryModel> getAllEntries(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataEntryModel> allEntries = new ArrayList<>();
        Cursor cr = db.rawQuery("select * from "+TABLE_NAME+";",null);
        try{
            while(cr.moveToNext())
                allEntries.add(new DataEntryModel(cr.getInt(0),cr.getString(1),cr.getInt(2),cr.getInt(3)));
        }finally {
            cr.close();
        }
        return allEntries;
    }
}
