package com.slebbers.dunl08.presenters;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.interfaces.ChecklistView;
import com.slebbers.dunl08.interfaces.ChecklistViewPresenter;
import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.model.ChecklistItem;
import com.slebbers.dunl08.network.ServerConnect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PresenterChecklistView implements ChecklistViewPresenter {

    private DatabaseAccessor db;
    private ServerConnect serverConnect;
    private ChecklistView view;
    private String equipmentID;
    private String checklistID;
    private Context context;

    private final String GOOD_TO_GO = "Good To Go";
    private final String DO_NOT_USE = "Do Not Use";

    public PresenterChecklistView(Context context, ChecklistView view) {
        db = new DatabaseAccessor(context);
        this.view = view;
        this.context = context;
        serverConnect = new ServerConnect();
    }

    @Override
    public void onStart() {
        view.displayEquipmentType(db.getEquipmentType(equipmentID));
        view.displayLastInspection(db.getLastInspection(equipmentID));
        view.displayNextInspection(db.getNextInspection(equipmentID));
        view.displayStatus(db.getChecklistStatus(equipmentID));

        // On start, all should be disabled until reinspect is clicked
        List<ChecklistItem> checklistItems = db.getChecklistItems(equipmentID);
        for(ChecklistItem item : checklistItems) {
            item.setIsEnabled(false);
        }

        view.displayChecklistItems(checklistItems);
    }

    @Override
    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    @Override
    public void btnReinspectClick() {
        List<ChecklistItem> checklistItems = db.getChecklistItems(equipmentID);
        for(ChecklistItem item : checklistItems) {
            item.setIsChecked("0");
            item.setIsEnabled(true);
        }

        db.clearCheckedStatus(db.getEquipmentChecklistID(equipmentID));
        db.clearNextInspection(equipmentID);
        view.displayLastInspection(SimpleDateFormat.getDateInstance().format(new Date()));
        view.displayChecklistItems(checklistItems);
    }

    @Override
    public void btnSubmitClick() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);

        // Last Inspection was today, next inspection is a month from now
        String lastInspection = SimpleDateFormat.getDateInstance().format(new Date());
        String nextInspection = SimpleDateFormat.getDateInstance().format(calendar.getTime());
        String checklistID = db.getEquipmentChecklistID(equipmentID);
        Checklist checklist = new Checklist();
        List<ChecklistItem> checkboxes = view.getCheckboxes();

        for(ChecklistItem item : checkboxes) {
            checklist.addChecklistItem(item);
        }

        checklist.setEquipmentID(equipmentID);
        checklist.setChecklistID(checklistID);
        checklist.setLastInspection(lastInspection);
        checklist.setNextInspection(nextInspection);

        // INSERT
        db.updateLastInspection(equipmentID, lastInspection);
        db.updateNextInspection(equipmentID, nextInspection);
        db.updateIsChecked(checklistID, checklist);

        if(isGoodToGo(checkboxes)) {
            checklist.setStatus(GOOD_TO_GO);
            db.updateEquipmentStatus(equipmentID, GOOD_TO_GO);
        } else {
            checklist.setStatus(DO_NOT_USE);
            db.updateEquipmentStatus(equipmentID, DO_NOT_USE);
        }

        String status = serverConnect.submitChecklist(checklist);

        if(status.equals("complete")) {
            Toast.makeText(context, "Saved at server. Rescan tag to verify results", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Could not sync to server...", Toast.LENGTH_LONG).show();
            Log.d("PCV Submit ERROR: ", status);
        }

        view.disableButtons();
    }

    private boolean isGoodToGo(List<ChecklistItem> checkboxes) {
        int numChecked = 0;

        for(ChecklistItem item : checkboxes) {
            if(item.getIsChecked().equals("1"))
                numChecked++;
        }

        return numChecked == checkboxes.size();
    }
}
