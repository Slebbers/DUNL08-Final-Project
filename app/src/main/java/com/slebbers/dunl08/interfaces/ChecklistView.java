package com.slebbers.dunl08.interfaces;

import android.widget.CheckBox;

import com.slebbers.dunl08.model.ChecklistItem;

import java.util.HashMap;
import java.util.List;

public interface ChecklistView {
    void displayEquipmentType(String equipmentType);
    void displayNextInspection(String date);
    void displayLastInspection(String date);
    void displayStatus(String status);
    void displayChecklistItems(List<ChecklistItem> checklistItems);
    void disableButtons();
    List<ChecklistItem> getCheckboxes();

}
