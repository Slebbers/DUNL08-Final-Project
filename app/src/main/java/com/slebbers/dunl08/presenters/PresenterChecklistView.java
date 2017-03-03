package com.slebbers.dunl08.presenters;

import android.content.Context;
import android.widget.CheckBox;

import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.interfaces.ChecklistView;
import com.slebbers.dunl08.interfaces.ChecklistViewPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PresenterChecklistView implements ChecklistViewPresenter {

    private DatabaseAccessor db;
    private ChecklistView view;
    private String equipmentID;
    private final String GOOD_TO_GO = "Good To Go";
    private final String DO_NOT_USE = "Do Not Use";

    public PresenterChecklistView(Context context, ChecklistView view) {
        db = new DatabaseAccessor(context);
        this.view = view;
    }

    @Override
    public void onStart() {
        view.displayEquipmentID(equipmentID);
        view.displayLastInspection(db.getLastInspection(equipmentID));
        view.displayNextInspection(db.getNextInspection(equipmentID));
        view.displayStatus(db.getChecklistStatus(equipmentID));
        view.displayChecklistItems(db.getChecklistItems(equipmentID));
    }

    @Override
    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    @Override
    public void btnReinspectClick() {
        db.clearCheckedStatus(equipmentID);
        db.clearNextInspection(equipmentID);
    }

    @Override
    public void btnSubmitClick() {
        db.updateLastInspection(equipmentID, SimpleDateFormat.getDateInstance().format(new Date()));

        // inspections are monthly
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        db.updateNextInspection(equipmentID, SimpleDateFormat.getDateInstance().format(calendar.getTime()));

        List<CheckBox> checkboxes = view.getCheckboxes();
        List<String> checkboxText = new ArrayList<>();
        List<Integer> isChecked = new ArrayList<>();

        for(CheckBox cb : checkboxes) {
            checkboxText.add(cb.getText().toString());
            if(cb.isChecked())
                isChecked.add(1);
            else
                isChecked.add(0);
        }

        db.updateIsChecked(equipmentID, checkboxText, isChecked);

        if(isGoodToGo(checkboxes))
            db.updateEquipmentStatus(equipmentID, GOOD_TO_GO);
        else
            db.updateEquipmentStatus(equipmentID, DO_NOT_USE);
    }

    private boolean isGoodToGo(List<CheckBox> checkboxes) {
        int numChecked = 0;

        for(CheckBox cb : checkboxes) {
            if(cb.isChecked())
                numChecked++;
        }

        return numChecked == checkboxes.size();
    }
}
