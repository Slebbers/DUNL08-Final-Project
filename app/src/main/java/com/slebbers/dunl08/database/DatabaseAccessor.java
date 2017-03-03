package com.slebbers.dunl08.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paul on 17/02/2017.
 */

public class DatabaseAccessor {

    private ChecklistDataDbHelper dbHelper;
    private SQLiteDatabase db;

    private final String EMPTY_STRING = "";
    private final String GOOD_TO_GO = "Good To Go";
    private final String DO_NOT_USE = "Do Not Use";

    public DatabaseAccessor(Context context) {
        dbHelper = new ChecklistDataDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void createEquipment() {

    }

    public boolean clearCheckedStatus(String equipmentID) {
        String clearCheckedStatusQuery = "UPDATE ChecklistItem SET IsChecked = 0 WHERE ChecklistID = " + equipmentID;
        Cursor clearCheckedStatusCursor = db.rawQuery(clearCheckedStatusQuery, null);

        if(clearCheckedStatusCursor.moveToFirst()) {
            clearCheckedStatusCursor.close();
            return true;
        }

        clearCheckedStatusCursor.close();
        return false;
    }

    public boolean clearNextInspection(String equipmentID) {
        String emptyNextInspectionQuery = "UPDATE Equipment SET NextInspection = NULL, Status = NULL WHERE EquipmentID = " + equipmentID;
        Cursor emptyNextInspectionCursor = db.rawQuery(emptyNextInspectionQuery, null);

        if(emptyNextInspectionCursor.moveToFirst()) {
            emptyNextInspectionCursor.close();
            return true;
        }

        emptyNextInspectionCursor.close();
        return false;
    }

    public void emptyStatus(String equipmentID) {

    }


    public boolean checkEquipmentExists(String equipmentID) {
        String query = "SELECT ChecklistItem FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";

        try {
            // if the equipment does not exist, an exception is thrown, so return false
            Cursor cursor = db.rawQuery(query, null);
            cursor.close();
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    // Returns a HashMap containing the checklist item as the key, as the checked status as the value
    public HashMap<String, Integer> getChecklistItems(String equipmentID) {
        HashMap<String, Integer> checklistItems = new HashMap<>();
        String checklistItemsQuery = "SELECT ChecklistItem, IsChecked FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";
        Cursor checklistItemsCursor = db.rawQuery(checklistItemsQuery, null);

        while (checklistItemsCursor.moveToNext()) {
            String item = checklistItemsCursor.getString(checklistItemsCursor.getColumnIndex(ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM));
            int isChecked = checklistItemsCursor.getInt(checklistItemsCursor.getColumnIndex(ChecklistDataContract.ChecklistItemEntry.COLUMN_ISCHECKED));
            checklistItems.put(item, isChecked);
        }


        checklistItemsCursor.close();
        return checklistItems;
    }

    public String getLastInspection(String equipmentID) {
        String lastInspectionQuery = "SELECT LastInspection FROM Equipment WHERE ChecklistID = " + equipmentID;
        Cursor lastInspectionCursor = db.rawQuery(lastInspectionQuery, null);

        if(lastInspectionCursor.moveToFirst()) {
            String lastInspection = lastInspectionCursor.getString(0);
            lastInspectionCursor.close();
            return lastInspection;
        } else {
            return EMPTY_STRING;
        }
    }

    public String getNextInspection(String equipmentID) {
        String nextInspectionQuery = "SELECT NextInspection FROM Equipment WHERE ChecklistID = " + equipmentID;
        Cursor nextInspectionCursor = db.rawQuery(nextInspectionQuery, null);

        if(nextInspectionCursor.moveToFirst()) {
            String nextInspection = nextInspectionCursor.getString(0);
            nextInspectionCursor.close();
            return nextInspection;
        }

        nextInspectionCursor.close();
        return EMPTY_STRING;
    }

    public String getChecklistStatus(String equipmentID) {
        String statusQuery = "SELECT Status FROM Equipment WHERE ChecklistID = " + equipmentID;
        Cursor statusCursor = db.rawQuery(statusQuery, null);

        if(statusCursor.moveToFirst()) {
            String status = statusCursor.getString(0);
            statusCursor.close();
            return status;
        }
        statusCursor.close();
        return EMPTY_STRING;
    }

    // TODO: Change to HashMap
    public boolean updateIsChecked(String equipmentID, List<String> checkboxes, List<Integer> isChecked) {
        String partialQuery = "UPDATE ChecklistItem SET IsChecked = ";
        Cursor isCheckedCursor;

        for(int i = 0; i < checkboxes.size(); i++) {
            String updateIsCheckedQuery = partialQuery + isChecked.get(i).toString() +
                    " WHERE ChecklistID = " + equipmentID +
                    " AND ChecklistItem = " +
                    "'" + checkboxes.get(i) + "'" ;

            isCheckedCursor = db.rawQuery(updateIsCheckedQuery, null);

            if(isCheckedCursor.moveToFirst()) {
                isCheckedCursor.close();
                return false;
            }

            isCheckedCursor.close();
        }
        return true;
    }

    public boolean updateLastInspection(String equipmentID, String date) {
        String lastInspectionQuery = "UPDATE Equipment SET LastInspection = "  +  "'" + date +  "'" + " WHERE EquipmentID = " + equipmentID;
        Cursor lastInspectionCursor = db.rawQuery(lastInspectionQuery, null);

        if(lastInspectionCursor.moveToFirst()) {
            lastInspectionCursor.close();
            return true;
        }

        lastInspectionCursor.close();
        return false;
    }

    public boolean updateNextInspection(String equipmentID, String date) {
        String nextInspectionQuery = "UPDATE Equipment SET NextInspection = "  +  "'" + date +  "'" + " WHERE EquipmentID = " + equipmentID;
        Cursor nextInspectionCursor = db.rawQuery(nextInspectionQuery, null);

        if(nextInspectionCursor.moveToFirst()) {
            nextInspectionCursor.close();
            return true;
        }

        nextInspectionCursor.close();
        return false;
    }

    public boolean updateEquipmentStatus(String equipmentID, String status) {
        String updateEquipmentQuery = "UPDATE Equipment SET Status = '" + status + "' WHERE EquipmentID = " + equipmentID;
        Cursor updateEquipmentCursor = db.rawQuery(updateEquipmentQuery, null);

        if(updateEquipmentCursor.moveToFirst()) {
            updateEquipmentCursor.close();
            return true;
        }

        updateEquipmentCursor.close();
        return false;
    }
}