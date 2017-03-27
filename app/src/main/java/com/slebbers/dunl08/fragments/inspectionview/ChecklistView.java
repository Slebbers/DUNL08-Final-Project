package com.slebbers.dunl08.fragments.inspectionview;

import com.slebbers.dunl08.model.ChecklistItem;
import java.util.List;

/**
 * Interface for the ChecklistView.
 */
public interface ChecklistView {
    /**
     * Displays the equipment type on screen
     * @param equipmentType The type of the equipment
     */
    void displayEquipmentType(String equipmentType);
    /**
     * Displays the next inspection date on screen
     * @param date The date of the next inspection
     */
    void displayNextInspection(String date);
    /**
     * Displays the last inspection on screen
     * @param date The date of the last inspection
     */
    void displayLastInspection(String date);
    void displayStatus(String status);

    /**
     * Displays items that make up the checklist
     * @param checklistItems The items within the checklist
     */
    void displayChecklistItems(List<ChecklistItem> checklistItems);
    /**
     * Disables buttons on screen after reinspection
     */
    void disableButtons();
    /**
     * Gets the Checkboxes current being displayed on screen
     * @return List<ChecklistItem> that are currently being displayed
     */
    List<ChecklistItem> getCheckboxes();
    /**
     * Displays a toast message on screen
     */
    void displayMessage(String message);
}
