package com.slebbers.dunl08.model;

/**
 * Data class that holds stores information related to items within a Checklist
 */

public class ChecklistItem {

    private String ChecklistItemID;
    private String ChecklistItem;
    private String IsChecked;
    private boolean IsEnabled;

    /**
     * Constructs a new instance of a ChecklistItem
     */
    public ChecklistItem() {

    }

    /**
     * Getter method for ChecklistItemID
     * @return The ID for the current ChecklistItem
     */
    public String getChecklistItemID() {
        return ChecklistItemID;
    }

    /**
     * Sets the ID for this instance of ChecklistItem
     * @param checklistItemID The ID for the ChecklistITem
     */
    public void setChecklistItemID(String checklistItemID) {
        ChecklistItemID = checklistItemID;
    }

    /**
     * Getter method to return the text of the ChecklistItem
     * @return String of text for the current ChecklistItem
     */
    public String getChecklistItem() {
        return ChecklistItem;
    }

    /**
     * Sets the text for the current checklist item (i.e. Check oil levels)
     * @param checklistItem String of text for the current ChecklistItem
     */
    public void setChecklistItem(String checklistItem) {
        ChecklistItem = checklistItem;
    }

    /**
     * Getter method to return the checked status of the ChecklistItem
     * @return 0 if not checked, 1 if checked
     */
    public String getIsChecked() {
        return IsChecked;
    }

    /**
     * Sets the isChecked value of the current ChecklistItem. Use 0 if unchecked and 1
     * if checked.
     * @param isChecked
     */
    public void setIsChecked(String isChecked) {
        IsChecked = isChecked;
    }

    /**
     * Getter method to find out if this item is enabled or not
     * @return {@code true} if enabled {@code false} if disabled
     */
    public boolean getIsEnabled() {
        return IsEnabled;
    }

    /**
     * Sets the enabled status of the current ChecklistItem
     * @param isEnabled {@code true} if enabled {@code false} if disabled
     */
    public void setIsEnabled(boolean isEnabled) {
        IsEnabled = isEnabled;
    }
}
