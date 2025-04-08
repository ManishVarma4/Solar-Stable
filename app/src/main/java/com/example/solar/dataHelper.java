package com.example.solar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class dataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "solar_db";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_NAME = "solar";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_CURR = "current";
    private static final String COLUMN_VOLT = "voltage";
    private static final String COLUMN_AREA = "area";
    private static final String COLUMN_IRR = "irradiance";
    private static final String COLUMN_EFF = "efficiency";

    public dataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BRAND + " TEXT, "
                + COLUMN_CURR + " REAL, "
                + COLUMN_VOLT + " REAL, "
                + COLUMN_AREA + " REAL, "
                + COLUMN_IRR + " REAL, "
                + COLUMN_EFF + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_EFF + " REAL DEFAULT 0");
            } catch (Exception e) {
                Log.e("Database", "Error upgrading DB: " + e.getMessage());
            }
        }
    }

    public boolean insertData(String brand, double curr, double volt, double area, double irr, double eff) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BRAND, brand);
        values.put(COLUMN_CURR, curr);
        values.put(COLUMN_VOLT, volt);
        values.put(COLUMN_AREA, area);
        values.put(COLUMN_IRR, irr);
        values.put(COLUMN_EFF, eff);

        long result = db.insert(TABLE_NAME, null, values);
        Log.d("Database", "Insert result: " + result);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
