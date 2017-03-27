package com.slebbers.dunl08.activities.main;

/**
 * Interface for the Main screen of the application.
 * Used to abstract the view implementation from the presenter.
 */

public interface MainView  {
    /**
     * Displays checklist information on screen
     * @param equipmentID The ID of the equipment to show
     */
    void showChecklist(String equipmentID);
    /**
     * Displays a toast message alerting the user that the
     * equipment could not be located
     */
    void equipmentNotFound();
    /**
     * Toggles tvScanTag betweeen View.VISIBLE and View.INVISBLE
     */
    void toggleScanTagVisibility();
    /**
     * Toggles pbSync betweeen View.VISIBLE and View.INVISBLE
     */
    void toggleProgressBarVisibility();
    /**
     * Toggles the initial syncing loader animation
     */
    void toggleSyncVisibility();

    /**
     * Updates the progress bar percentage
     * @param progress The progress that has been made
     */
    void setPbProgress(int progress);

    /**
     * Sets the text of tvSync
     * @param text
     */
    void setSyncText(String text);

    /**
     * Displays a toast message to the user
     * @param message The message to be displayed
     */
    void displayMessage(String message);
}