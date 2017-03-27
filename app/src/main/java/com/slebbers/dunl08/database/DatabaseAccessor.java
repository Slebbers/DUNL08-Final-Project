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

/**
 * DatabaseAccessor provides methods for interacting with the local SQLite Database.
 */
public class DatabaseAccessor {

    private ChecklistDataDbHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Constructs a new instance of DatabaseAccessor
     * @param context The current Android context
     */
    public DatabaseAccessor(Context context) {
        dbHelper = new ChecklistDataDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Adds a new piece of equipment into the database
     * @param checklistID The ID of the Checklist to be assigned to the Equipment
     * @param equipmentID The ID of the equipment to be added
     * @param equipmentName The name of the equipment to be added
     */
    public void addEquipment(String checklistID, String equipmentID, String equipmentName) {
        String equipmentInsert = "INSERT INTO Equipment(EquipmentID, EquipmentName, ChecklistID) " +
                "VALUES(" + equipmentID + ", '" + equipmentName + "', " + checklistID + ")";
        db.execSQL(equipmentInsert);
    }

    /**
     * Adds data to the EquipmentItem table to keep track of which items have been checked
     * @param equipmentID The ID of the equipment
     * @param checklistItems The items that make up the Checklist assigned to the equipment ID
     */
    public void insertEquipmentItem(String equipmentID, List<ChecklistItem> checklistItems) {
        String insertEquipmentItem = "INSERT INTO EquipmentItem(EquipmentID, ChecklistItemID, IsChecked) VALUES (";

        for(ChecklistItem checklistItem : checklistItems) {
            String fullQuery = insertEquipmentItem + equipmentID + ", " + checklistItem.getChecklistItemID() + ", " + checklistItem.getIsChecked() + ")";
            db.execSQL(fullQuery);
        }

    }

    /**
     * Inserts items assigned to a Checklist into the database.
     * @param checklistID The ID of the Checklist
     * @param checklistItems The items that make up the Checklist
     */
    public void insertChecklistItems(String checklistID, List<ChecklistItem> checklistItems) {
        String insertQuery = "INSERT INTO ChecklistItem(ChecklistItemID, ChecklistItem, ChecklistID) VALUES (";

        for(ChecklistItem checklistItem : checklistItems) {
            String fullQuery = insertQuery + checklistItem.getChecklistItemID() +  ", '" + checklistItem.getChecklistItem() + "', " + checklistID + ")";
            db.execSQL(fullQuery);
        }
    }

    /**
     * Sets all ChecklistItems within a Checklist to 0
     * @param checklistID
     * @return {@code True if items are cleared} or {@code False if no items exist}
     */
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

    /**
     * Clears the date of the equipments next inspection
     * @param equipmentID The ID of the equipment
     * @return {@code true if cleared} or {@code false if no date exists}
     */
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

    /**
     * Checks if a equipment exists within the database
     * @param equipmentID ID of the equipment to be checked
     * @return {@code True if equipment exists} or {@code False if it does not exist}
     */
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

    /**
     * Gets the items that make up a Checklist
     * @param equipmentID The ID of the equipment
     * @return A List of items that make up a Checklist
     */
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
            if(isCheckedCursor.moveToNext()) {
                item.setIsChecked(isCheckedCursor.getString(isCheckedCursor.getColumnIndex("IsChecked")));
               // item.setIsChecked(Integer.toString(isCheckedCursor.getInt(isCheckedCursor.getColumnIndex("IsChecked"))));
            }

            isCheckedCursor.close();
            checklistItems.add(item);
        }

        checklistItemsCursor.close();
        return checklistItems;
    }

    /**
     * Gets the last inspection of an equipment
     * @param equipmentID The ID of the equipment
     * @return The last inspection of the equipment
     */
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

    /**
     * Gets the next inspection of an equipment
     * @param equipmentID The ID of the equipment
     * @return The next inspection of the equipment
     */
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

    /**
     * Gets the status a current checklist
     * @param equipmentID the ID of the equipment
     * @return {@code "Good to Go"} or {@code "Do Not Use"}
     */
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

    /**
     * Updates the IsChecked field of ChecklistItems
     * @param checklist The Checklist to be updated
     * @return {@code True if updated} or {@false If no items exist}
     */
    public boolean updateIsChecked(Checklist checklist) {
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

    /**
     * Updates the date of the next inspection of an equipment
     * @param equipmentID The ID of the equipment
     * @param date The date of the next inspection
     * @return {@code True if updated} or {@code False if no last inspection exists}
     */
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

    /**
     * Updates the next inspection date of an equipment
     * @param equipmentID The ID of the equipment
     * @param date The date of the next inspection
     * @return {@code True if updated} or {@code False if no next inspection exists}
     */
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

    /**
     * Updates the status of an equipment. Status should be "Good to Go" or "Do Not Use"
     * @param equipmentID The ID of the equipment
     * @param status The status of the equipment
     * @return {@code True if updated} or {@code False if no status exists}
     */
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

    /**
     * Gets the checklistID assigned to an equipment
     * @param equipmentID The ID of the equipment
     * @return The ID of the Checklist that is assigned to the equipment
     */
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

    /**
     * Gets the name of the equipment
     * @param equipmentID The ID of the equipment
     * @return The name of the equipment
     */
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

    /**
     * Gets the data that can only be changed by the app to submit to the server
     * @return A List of Checklists saved locally
     */
    public List<Checklist> getLocalDataForServer() {
        //
        String getIsCheckedQuery = "SELECT Equipment.EquipmentID, Equipment.ChecklistID, Equipment.LastInspection, Equipment.NextInspection, Equipment.Status, EquipmentItem.ChecklistItemID, EquipmentItem.IsChecked "
                + "FROM Equipment, EquipmentItem WHERE Equipment.EquipmentID = EquipmentItem.EquipmentID";
        Cursor getIsCheckedCursor = db.rawQuery(getIsCheckedQuery, null);
        List<Checklist> checklists = new ArrayList<>();
        Checklist currentChecklist;
        ChecklistItem currentItem;
        int count = 0;

        while(getIsCheckedCursor.moveToNext()) {
            String equipmentID = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("EquipmentID"));
            String checklistID = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("ChecklistID"));
            String lastInspection = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("LastInspection"));
            String nextInspection = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("NextInspection"));
            String status = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("Status"));
            String checklistItemID = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("ChecklistItemID"));
            String isChecked = getIsCheckedCursor.getString(getIsCheckedCursor.getColumnIndex("IsChecked"));

            // Construct a new Checklist
            if(checklists.isEmpty()) {
                currentChecklist = new Checklist();
                currentChecklist.setEquipmentID(equipmentID);
                currentChecklist.setChecklistID(checklistID);
                currentChecklist.setLastInspection(lastInspection);
                currentChecklist.setNextInspection(nextInspection);
                currentChecklist.setStatus(status);
                currentItem = new ChecklistItem();
                currentItem.setChecklistItemID(checklistItemID);
                currentItem.setIsChecked(isChecked);
                currentChecklist.addChecklistItem(currentItem);
                checklists.add(currentChecklist);
            } else {
                if(checklists.get(count).getChecklistID().equals(checklistID)) {
                    currentItem = new ChecklistItem();
                    currentItem.setChecklistItemID(checklistItemID);
                    currentItem.setIsChecked(isChecked);
                    checklists.get(count).addChecklistItem(currentItem);
                } else {
                    currentChecklist = new Checklist();
                    currentChecklist.setEquipmentID(equipmentID);
                    currentChecklist.setChecklistID(checklistID);
                    currentChecklist.setLastInspection(lastInspection);
                    currentChecklist.setNextInspection(nextInspection);
                    currentChecklist.setStatus(status);
                    currentItem = new ChecklistItem();
                    currentItem.setChecklistItemID(checklistItemID);
                    currentItem.setIsChecked(isChecked);
                    currentChecklist.addChecklistItem(currentItem);
                    checklists.add(currentChecklist);
                    count++;
                }
            }
        }

        return checklists;
    }

    /**
     * This method returns data nessessary to display the details in FragmentViewChecklists
     * @return A list of checklists
     */
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