package com.slebbers.dunl08.interfaces;

import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;

public interface ChecklistView {
    void displayEquipmentID(String equipmentID);
    void displayNextInspection(String date);
    void displayLastInspection(String date);
    void displayStatus(String status);
    void displayChecklistItems(HashMap<String, Integer> checklistItems);
    void disableButtons();
    List<CheckBox> getCheckboxes();

}
