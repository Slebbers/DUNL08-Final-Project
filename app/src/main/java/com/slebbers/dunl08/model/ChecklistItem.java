package com.slebbers.dunl08.model;

/**
 * Created by Paul on 06/03/2017.
 */

public class ChecklistItem {

    private String ChecklistItemID;
    private String ChecklistItem;
    private String IsChecked;

    public ChecklistItem() {

    }

    public String getChecklistItemID() {
        return ChecklistItemID;
    }

    public void setChecklistItemID(String checklistItemID) {
        ChecklistItemID = checklistItemID;
    }

    public String getChecklistItem() {
        return ChecklistItem;
    }

    public void setChecklistItem(String checklistItem) {
        ChecklistItem = checklistItem;
    }

    public String getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(String isChecked) {
        IsChecked = isChecked;
    }
}
