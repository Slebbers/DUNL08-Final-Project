package com.slebbers.dunl08.fragments.inspectionview;

/**
 * Interface for the presenter of the ChecklistView
 */
public interface ChecklistViewPresenter {
    /**
     * Represents the onStart part of the Activity lifecycle
     */
    void onStart();
    /**
     * Sets the equipmentID for the checklist to gather data from the database
     * @param equipmentID
     */
    void setEquipmentID(String equipmentID);
    /**
     * Processes interaction when the reinspect button is clicked
     */
    void btnReinspectClick();

    /**
     * Processes interaction when btnSubmit is clicked
     * @param networkAvailable {@code True if network is available} or {@code False if unavailable}
     */
    void btnSubmitClick(boolean networkAvailable);
}
