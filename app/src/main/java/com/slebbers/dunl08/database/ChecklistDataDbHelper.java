package com.slebbers.dunl08.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.slebbers.dunl08.database.ChecklistDataContract.*;

/*    Checklist
*     (PK) ChecklistID int
*
*     Checklist (Many)->  ChecklistItem
*     (PK) ChecklistItemID integer
*     (FK) ChecklistID integer
*          ChecklistItem text
*
*     Equipment -> Checklist
*     (PK) EquipmentID integer
*     (FK) ChecklistID integer
*          LastInspection date
*          NextInspection date
*          Status text
*
 */

public class ChecklistDataDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Checklist.db";
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_TABLE_CHECKLIST =
            "CREATE TABLE " + ChecklistEntry.TABLE_NAME + " (" +
                    ChecklistEntry.COLUMN_CHECKLIST_ID_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ChecklistEntry.COLUMN_EQUIPMENT_ID + " INTEGER)";

    public static final String SQL_CREATE_TABLE_CHECKLISTITEM =
            "CREATE TABLE " + ChecklistItemEntry.TABLE_NAME + " (" +
            ChecklistItemEntry.COLUMN_CHECKLISTITEM_ID_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ChecklistItemEntry.COLUMN_CHECKLISTITEM + " TEXT NOT NULL, " +
            ChecklistItemEntry.COLUMN_ISCHECKED + " INTEGER NOT NULL, " +
            ChecklistItemEntry.COLUMN_CHECKLIST_ID_FK + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ChecklistItemEntry.COLUMN_CHECKLIST_ID_FK + ") " +
            "REFERENCES " + ChecklistEntry.TABLE_NAME + "(" + ChecklistItemEntry.COLUMN_CHECKLIST_ID_FK + "))";

    public static final String SQL_CREATE_TABLE_EQUIPMENT =
            "CREATE TABLE " + EquipmentEntry.TABLE_NAME + " (" +
            EquipmentEntry.COLUMN_EQUIPMENT_ID_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EquipmentEntry.COLUMN_EQUIPMENT_TYPE + " TEXT, " +
            EquipmentEntry.COLUMN_LAST_INSPECTION + " TEXT, " +
            EquipmentEntry.COLUMN_NEXT_INSPECTION + " TEXT, " +
            EquipmentEntry.COLUMN_PASS_FAIL + " TEXT, " +
            EquipmentEntry.COLUMN_CHECKLIST_ID_FK + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + EquipmentEntry.COLUMN_CHECKLIST_ID_FK + ") " +
            "REFERENCES " + ChecklistEntry.TABLE_NAME + "(" + EquipmentEntry.COLUMN_CHECKLIST_ID_FK + "))";


    public ChecklistDataDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CHECKLIST);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CHECKLISTITEM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EQUIPMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("ALTER TABLE EQUIPMENT ADD COLUMN EquipmentType");
    }
}
