package com.slebbers.dunl08.presenters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slebbers.dunl08.R;
import com.slebbers.dunl08.database.ChecklistDataContract;
import com.slebbers.dunl08.database.ChecklistDataDbHelper;
import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.interfaces.MainView;
import com.slebbers.dunl08.interfaces.Presenter;
import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.network.ServerConnect;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistEntry.COLUMN_CHECKLIST_ID_PK;
import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistEntry.COLUMN_EQUIPMENT_ID;
import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM;
import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM_ID_PK;
import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLIST_ID_FK;
import static com.slebbers.dunl08.database.ChecklistDataContract.ChecklistItemEntry.COLUMN_ISCHECKED;
import static com.slebbers.dunl08.database.ChecklistDataContract.EquipmentEntry.COLUMN_EQUIPMENT_ID_PK;
import static com.slebbers.dunl08.database.ChecklistDataContract.EquipmentEntry.COLUMN_EQUIPMENT_TYPE;
import static com.slebbers.dunl08.database.ChecklistDataContract.EquipmentEntry.COLUMN_LAST_INSPECTION;
import static com.slebbers.dunl08.database.ChecklistDataContract.EquipmentEntry.COLUMN_NEXT_INSPECTION;
import static com.slebbers.dunl08.database.ChecklistDataContract.EquipmentEntry.COLUMN_PASS_FAIL;


public class PresenterMain implements Presenter {

    private MainView view;
    private ChecklistDataDbHelper dbHelper;
    private Context context;
    private ServerConnect connection;
    private DatabaseAccessor db;

    public PresenterMain(Context context, MainView view) {
        this.context = context;
        this.view = view;
        dbHelper = new ChecklistDataDbHelper(context);
        connection = new ServerConnect();
        db = new DatabaseAccessor(context);
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void syncDatabase() {
        checkDatabase(connection.getServerDatabaseJSON());
    }

    @Override
    public void onOptionsItemSelected(int id) {
        switch(id) {
            case R.id.action_settings:
                break;
            case R.id.action_connect:
                checkDatabase(connection.getServerDatabaseJSON());
            default:
                break;
        }
    }

    private String connect() {
        return connection.getServerDatabaseJSON();
    }

    private void checkDatabase(String serverJSON)  {
        Type listType = new TypeToken<ArrayList<Checklist>>(){}.getType();
        List<Checklist> checklists = new Gson().fromJson(serverJSON, listType);
        Log.d("returnedData", checklists.toString());

        for(int i = 0; i < checklists.size(); i++) {
            if(!db.checkEquipmentExists(checklists.get(i).getEquipmentID())) {
                String equipmentID = checklists.get(i).getEquipmentID();
                String checklistID = checklists.get(i).getChecklistID();
                String equipmentType = checklists.get(i).getEquipmentType();

                // insert
                db.addChecklistAndEquipment(checklistID, equipmentID, equipmentType);
                db.updateLastInspection(equipmentID, checklists.get(i).getLastInspection());
                db.updateNextInspection(equipmentID, checklists.get(i).getNextInspection());
                db.updateEquipmentStatus(equipmentID, checklists.get(i).getStatus());
                db.insertChecklistItems(checklistID, checklists.get(i).getChecklistItems());
            }
        }
    }

    @Override
    public void bindView(MainView mainView) {
        view = mainView;
    }

    @Override
    public void setupNFC() {

    }

    @Override
    public void onTagScanned(Intent intent) {

    }

    @Override
    public void onCheckboxClicked() {

    }

    @Override
    public void onSaveClicked() {

    }

    private void writeStuffToDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        /*
            CHECKLIST
         */

        // Since I'm using 3 separate nfc tags, 3 will do for now
        values.put(COLUMN_CHECKLIST_ID_PK, 1);
        values.put(COLUMN_EQUIPMENT_ID, 1);
        db.insert(ChecklistDataContract.ChecklistEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLIST_ID_PK, 2);
        values.put(COLUMN_EQUIPMENT_ID, 2);
        db.insert(ChecklistDataContract.ChecklistEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLIST_ID_PK, 3);
        values.put(COLUMN_EQUIPMENT_ID, 3);
        db.insert(ChecklistDataContract.ChecklistEntry.TABLE_NAME, null, values);
        values.clear();


        /*
            CHECKLIST ITEM
         */

        // NO BOOLEAN TYPE IN SQLITE, 0 = FALSE, 1 = TRUE;
        // one ROW
        values.put(COLUMN_CHECKLISTITEM_ID_PK, 1);
        values.put(COLUMN_CHECKLIST_ID_FK, 1);
        values.put(COLUMN_CHECKLISTITEM, "Equipment turns on");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 2);
        values.put(COLUMN_CHECKLIST_ID_FK, 1);
        values.put(COLUMN_CHECKLISTITEM, "Cable not damaged");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 3);
        values.put(COLUMN_CHECKLIST_ID_FK, 2);
        values.put(COLUMN_CHECKLISTITEM, "Cable secured to floor");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 4);
        values.put(COLUMN_CHECKLIST_ID_FK, 2);
        values.put(COLUMN_CHECKLISTITEM, "Operation manual available");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();


        values.put(COLUMN_CHECKLISTITEM_ID_PK, 5);
        values.put(COLUMN_CHECKLIST_ID_FK, 2);
        values.put(COLUMN_CHECKLISTITEM, "Lights operable");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 6);
        values.put(COLUMN_CHECKLIST_ID_FK, 3);
        values.put(COLUMN_CHECKLISTITEM, "Eyewear available");
        values.put(COLUMN_ISCHECKED, 1);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 7);
        values.put(COLUMN_CHECKLIST_ID_FK, 3);
        values.put(COLUMN_CHECKLISTITEM, "No wobbles or shakes");
        values.put(COLUMN_ISCHECKED, 0);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 8);
        values.put(COLUMN_CHECKLIST_ID_FK, 3);
        values.put(COLUMN_CHECKLISTITEM, "Equipment is clean");
        values.put(COLUMN_ISCHECKED, 0);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(COLUMN_CHECKLISTITEM_ID_PK, 9);
        values.put(COLUMN_CHECKLIST_ID_FK, 3);
        values.put(COLUMN_CHECKLISTITEM, "Parts have been serviced");
        values.put(COLUMN_ISCHECKED, 0);
        db.insert(ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, null, values);
        values.clear();

        /*
            EQUIPMENT
         */
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);

        values.put(COLUMN_EQUIPMENT_ID_PK, 1);
        values.put(COLUMN_EQUIPMENT_TYPE, "Forklift");
        values.put(COLUMN_CHECKLIST_ID_FK, 1);
        values.put(COLUMN_LAST_INSPECTION, dateFormat.format(date));

        c.add(Calendar.MONTH, 1);
        date = c.getTime();

        values.put(COLUMN_NEXT_INSPECTION, dateFormat.format(date));
        values.put(COLUMN_PASS_FAIL, "Good To Go");
        db.insert(ChecklistDataContract.EquipmentEntry.TABLE_NAME, null, values);

        // back to today
        c.add(Calendar.MONTH, -1);
        date = c.getTime();

        values.put(COLUMN_EQUIPMENT_ID_PK, 2);
        values.put(COLUMN_EQUIPMENT_TYPE, "Harness");
        values.put(COLUMN_CHECKLIST_ID_FK, 2);
        values.put(COLUMN_LAST_INSPECTION, dateFormat.format(date));
        c.add(Calendar.MONTH, 1);
        date = c.getTime();
        values.put(COLUMN_NEXT_INSPECTION, dateFormat.format(date));
        values.put(COLUMN_PASS_FAIL, "Good To Go");

        db.insert(ChecklistDataContract.EquipmentEntry.TABLE_NAME, null, values);

        // back to today
        c.add(Calendar.MONTH, -1);
        date = c.getTime();

        values.put(COLUMN_EQUIPMENT_ID_PK, 3);
        values.put(COLUMN_EQUIPMENT_TYPE, "Excavator");
        values.put(COLUMN_CHECKLIST_ID_FK, 3);
        values.put(COLUMN_LAST_INSPECTION, dateFormat.format(date));
        c.add(Calendar.MONTH, 1);
        date = c.getTime();
        values.put(COLUMN_NEXT_INSPECTION, dateFormat.format(date));
        values.put(COLUMN_PASS_FAIL, "Do Not Use");

        db.insert(ChecklistDataContract.EquipmentEntry.TABLE_NAME, null, values);

        Log.d("PresenterMain", "data inserted");
    }
}
