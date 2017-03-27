package com.slebbers.dunl08.activities.main;

import android.nfc.NdefRecord;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slebbers.dunl08.R;
import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.network.ServerConnect;
import com.slebbers.dunl08.nfc.NFCHelper;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Presenter. Handles the processing and interactions within
 * the MainView.
 */
public class PresenterMain implements Presenter {

    private MainView view;
    private ServerConnect connection;
    private DatabaseAccessor db;
    private NFCHelper nfcHelper;

    public PresenterMain(MainView view, DatabaseAccessor db) {
        this.view = view;
        this.db = db;
        connection = new ServerConnect();
        nfcHelper = new NFCHelper();
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
    public void firstLaunch() {
        toggleViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                checkDatabase(connection.getServerDatabaseJSON());
            }
        }).start();

        Handler delay = new Handler();
        // Added some delay here so it is apparent to the user that the database is being synced.
        // As we are only downloading < 100Kb of JSON text from the server this will be finished
        // very quickly in most cases.
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setPbProgress(100);
                view.setSyncText("Sync completed!");
            }
        }, 2000);

        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleViews();
            }
        }, 5000);
    }

    /**
     * Toggles the visibility of MainView elements
     */
    private void toggleViews() {
        view.toggleScanTagVisibility();
        view.toggleSyncVisibility();
        view.toggleProgressBarVisibility();
    }

    @Override
    public void onOptionsItemSelected(int id) {
        switch(id) {
            case R.id.action_connect:
                // first we must submit the checklist data we have locally, incase it was not submitted during
                // network failure
                view.displayMessage("Connecting to server...");
                connection.submitLocalData(db.getLocalDataForServer());
                checkDatabase(connection.getServerDatabaseJSON());
                view.displayMessage("Sync complete.");
            default:
                break;
        }
    }

    /**
     * Checks the database for equipment entries and adds them if they do not exist
     * @param serverJSON The JSON returned from the server
     */
    private void checkDatabase(String serverJSON)  {
        Type listType = new TypeToken<ArrayList<Checklist>>(){}.getType();
        List<Checklist> checklists = new Gson().fromJson(serverJSON, listType);
        Log.d("returnedData", checklists.toString());

        for(int i = 0; i < checklists.size(); i++) {
            if(!db.checkEquipmentExists(checklists.get(i).getEquipmentID())) {
                String equipmentID = checklists.get(i).getEquipmentID();
                String checklistID = checklists.get(i).getChecklistID();
                String equipmentName = checklists.get(i).getEquipmentName();

                // insert
                db.addEquipment(checklistID, equipmentID, equipmentName);
                db.updateLastInspection(equipmentID, checklists.get(i).getLastInspection());
                db.updateNextInspection(equipmentID, checklists.get(i).getNextInspection());
                db.updateEquipmentStatus(equipmentID, checklists.get(i).getStatus());
                db.insertChecklistItems(checklistID, checklists.get(i).getChecklistItems());
                db.insertEquipmentItem(equipmentID, checklists.get(i).getChecklistItems());
            }
        }
    }

    @Override
    public void onTagScanned(NdefRecord record) {
        view.toggleScanTagVisibility();

        try {
            String tagID = nfcHelper.readTag(record);

            if(db.checkEquipmentExists(tagID)) {
                view.showChecklist(tagID);
            } else {
                view.equipmentNotFound();
                view.toggleScanTagVisibility();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}