package com.slebbers.dunl08.fragments.inspectionview;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.fragments.inspectionview.ChecklistView;
import com.slebbers.dunl08.fragments.inspectionview.ChecklistViewPresenter;
import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.model.ChecklistItem;
import com.slebbers.dunl08.network.ServerConnect;

import java.text.SimpleDateFormat;
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
        view.displayEquipmentType(db.getEquipmentName(equipmentID));
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

        //db.clearCheckedStatus(db.getEquipmentChecklistID(equipmentID));
        db.clearCheckedStatus(equipmentID); // pass in equipmentID instead
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

        if(isGoodToGo(checkboxes)) {
            checklist.setStatus(GOOD_TO_GO);
            db.updateEquipmentStatus(equipmentID, GOOD_TO_GO);
            db.updateNextInspection(equipmentID, nextInspection);
            view.displayStatus(GOOD_TO_GO);
            view.displayNextInspection(nextInspection);
        } else {
            checklist.setStatus(DO_NOT_USE);
            db.updateEquipmentStatus(equipmentID, DO_NOT_USE);
            // Since the inspection has failed, the next inspection day will be tomorrow
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 1);
            nextInspection = SimpleDateFormat.getDateInstance().format(calendar.getTime());
            checklist.setNextInspection(nextInspection);
            db.updateNextInspection(equipmentID, nextInspection);
            view.displayStatus(DO_NOT_USE);
            view.displayNextInspection(nextInspection);
        }

        db.updateLastInspection(equipmentID, lastInspection);
        db.updateIsChecked(checklistID, checklist);

        for(ChecklistItem item : checkboxes) {
            item.setIsEnabled(false);
        }

        view.displayChecklistItems(checkboxes);

        // Do this on a new thread
        String status = serverConnect.submitChecklist(checklist);

        if(status.equals("complete")) {
            Toast.makeText(context, "Checklist saved!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Could not contact server, checklist saved locally", Toast.LENGTH_LONG).show();
            Log.d("PCV Submit ERROR: ", status);
        }

        view.disableButtons();
        view.displayLastInspection(lastInspection);


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
