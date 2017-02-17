package com.slebbers.dunl08.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by Paul on 17/02/2017.
 */

public class DatabaseAccessor {

    private ChecklistDataDbHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseAccessor(Context context) {
        dbHelper =  new ChecklistDataDbHelper(context);
      //  db = dbHelper.getReadableDatabase();
    }

    public boolean checkEquipmentExists(String equipmentID) {
        String query = "SELECT ChecklistItem FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";

        try {
            Cursor cursor = db.rawQuery(query, null);
            cursor.close();
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public void setWriteMode() {
        db = dbHelper.getWritableDatabase();
    }

    public void setReadMode() {
        db = dbHelper.getReadableDatabase();
    }
}




