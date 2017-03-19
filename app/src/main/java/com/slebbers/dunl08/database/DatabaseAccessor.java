package com.slebbers.dunl08.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;
import android.widget.CheckBox;

import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.model.ChecklistItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DatabaseAccessor {

    private ChecklistDataDbHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseAccessor(Context context) {
        dbHelper = new ChecklistDataDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addEquipment(String checklistID, String equipmentID, String equipmentType) {
        String equipmentInsert = "INSERT INTO Equipment(EquipmentID, EquipmentName, ChecklistID) " +
                "VALUES(" + equipmentID + ", '" + equipmentType + "', " + checklistID + ")";
        db.execSQL(equipmentInsert);
    }

    public void insertEquipmentItem(String equipmentID, List<ChecklistItem> checklistItems) {
        String insertEquipmentItem = "INSERT INTO EquipmentItem(EquipmentID, ChecklistItemID, IsChecked) VALUES (";

        for(ChecklistItem checklistItem : checklistItems) {
            String fullQuery = insertEquipmentItem + equipmentID + ", " + checklistItem.getChecklistItemID() + ", " + checklistItem.getIsChecked() + ")";
            db.execSQL(fullQuery);
        }

    }

    public void insertChecklistItems(String checklistID, List<ChecklistItem> checklistItems) {
        String insertQuery = "INSERT INTO ChecklistItem(ChecklistItemID, ChecklistItem, ChecklistID) VALUES(";

        for(ChecklistItem checklistItem : checklistItems) {
            String fullQuery = insertQuery + checklistItem.getChecklistItemID() + ", '" +checklistItem.getChecklistItem() + "', " + checklistID + ")";
            db.execSQL(fullQuery);
        }
    }

    public boolean clearCheckedStatus(String checklistID) {
        String clearCheckedStatusQuery = "UPDATE EquipmentItem SET IsChecked = 0 WHERE EquipmentID = " + checklistID;
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

    public boolean checkEquipmentExists(String equipmentID) {
        // If the supplied equipmentID is not an integer, it will cause an SQLiteException due
        // missing quotes around the number in the query
        try {
            Integer.parseInt(equipmentID);
        } catch (NumberFormatException e) {
            return false;
        }

        String query = "SELECT EquipmentID FROM Equipment WHERE EquipmentID = " + equipmentID;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            return true;
        }
    }

    // REDO
    public List<ChecklistItem> getChecklistItems(String equipmentID) {
        List<ChecklistItem> checklistItems = new ArrayList<>();
        ChecklistItem item;
        String checklistItemsQuery = "SELECT ChecklistItemID, ChecklistItem FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";
        Cursor checklistItemsCursor = db.rawQuery(checklistItemsQuery, null);

        while (checklistItemsCursor.moveToNext()) {
            item = new ChecklistItem();
            item.setChecklistItemID(checklistItemsCursor.getString(checklistItemsCursor.getColumnIndex("ChecklistItemID")));
            item.setChecklistItem(checklistItemsCursor.getString(checklistItemsCursor.getColumnIndex("ChecklistItem")));
            String checklistItemID = checklistItemsCursor.getString(checklistItemsCursor.getColumnIndex("ChecklistItemID"));
            Cursor isCheckedCursor = db.rawQuery("SELECT IsChecked FROM EquipmentItem WHERE ChecklistItemID = " + checklistItemID, null);
            if(isCheckedCursor.moveToFirst()) {
                item.setIsChecked(Integer.toString(isCheckedCursor.getInt(isCheckedCursor.getColumnIndex("IsChecked"))));
            }

            isCheckedCursor.close();
            checklistItems.add(item);
        }

        checklistItemsCursor.close();
        return checklistItems;
    }

    public String getLastInspection(String equipmentID) {
        String lastInspectionQuery = "SELECT LastInspection FROM Equipment WHERE EquipmentID = " + equipmentID;
        Cursor lastInspectionCursor = db.rawQuery(lastInspectionQuery, null);

        if(lastInspectionCursor.moveToNext()) {
            String lastInspection = lastInspectionCursor.getString(lastInspectionCursor.getColumnIndex("LastInspection"));
            lastInspectionCursor.close();
            return lastInspection;
        } else {
            lastInspectionCursor.close();
            return "";
        }
    }

    public String getNextInspection(String equipmentID) {
        String nextInspectionQuery = "SELECT NextInspection FROM Equipment WHERE EquipmentID = " + equipmentID;
        Cursor nextInspectionCursor = db.rawQuery(nextInspectionQuery, null);

        if(nextInspectionCursor.moveToNext()) {
            String lastInspection = nextInspectionCursor.getString(nextInspectionCursor.getColumnIndex("NextInspection"));
            nextInspectionCursor.close();
            return lastInspection;
        } else {
            nextInspectionCursor.close();
            return "";
        }
    }

    public String getChecklistStatus(String equipmentID) {
        String statusQuery = "SELECT Status FROM Equipment WHERE EquipmentID = " + equipmentID;
        Cursor statusCursor = db.rawQuery(statusQuery, null);

        if(statusCursor.moveToNext()) {
            String lastInspection = statusCursor.getString(statusCursor.getColumnIndex("Status"));
            statusCursor.close();
            return lastInspection;
        } else {
            statusCursor.close();
            return "";
        }
    }

    public boolean updateIsChecked(String checklistID, Checklist checklist) {
        String partialQuery = "UPDATE EquipmentItem SET IsChecked = ";
        Cursor isCheckedCursor;

        for(int i = 0; i < checklist.getChecklistItems().size(); i++) {
            ChecklistItem item = checklist.getChecklistItems().get(i);

            String updateIsCheckedQuery = partialQuery + item.getIsChecked() +
                    " WHERE ChecklistItemID = " + item.getChecklistItemID();

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

    public String getEquipmentChecklistID(String equipmentID) {
        String getChecklistIDQuery = "SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID;
        Cursor checklistIDCusor = db.rawQuery(getChecklistIDQuery, null);

        if(checklistIDCusor.moveToNext()) {
            String checklistID = checklistIDCusor.getString(checklistIDCusor.getColumnIndex("ChecklistID"));
            checklistIDCusor.close();
            return checklistID;
        } else {
            checklistIDCusor.close();
            return null;
        }
    }

    public String getEquipmentName(String equipmentID) {
        String getEquipmentNameQuery = "SELECT EquipmentName FROM Equipment WHERE EquipmentID = " + equipmentID;
        Cursor getEquipmentNameCursor = db.rawQuery(getEquipmentNameQuery, null);

        if(getEquipmentNameCursor.moveToNext()) {
            String equipmentType = getEquipmentNameCursor.getString(getEquipmentNameCursor.getColumnIndex("EquipmentName"));
            getEquipmentNameCursor.close();
            return equipmentType;
        } else {
            getEquipmentNameCursor.close();
            return null;
        }
    }

    public List<Checklist> getCardData() {
        String equipmentQuery = "SELECT * FROM EQUIPMENT";
        Cursor equipmentCursor = db.rawQuery(equipmentQuery, null);
        List<Checklist> checklists = new ArrayList<>();
        Checklist checklist;

        while(equipmentCursor.moveToNext()) {
            checklist = new Checklist();
            checklist.setEquipmentName(equipmentCursor.getString(equipmentCursor.getColumnIndex("EquipmentName")));
            checklist.setLastInspection(equipmentCursor.getString(equipmentCursor.getColumnIndex("LastInspection")));
            checklist.setNextInspection(equipmentCursor.getString(equipmentCursor.getColumnIndex("NextInspection")));
            checklist.setStatus(equipmentCursor.getString(equipmentCursor.getColumnIndex("Status")));
            checklist.setEquipmentID(equipmentCursor.getString(equipmentCursor.getColumnIndex("EquipmentID")));
            checklist.setChecklistID(equipmentCursor.getString(equipmentCursor.getColumnIndex("ChecklistID")));
            checklists.add(checklist);
        }

        Log.d("DB", "Checklist size: " + checklists.size());
        equipmentCursor.close();
        return checklists;
    }
}