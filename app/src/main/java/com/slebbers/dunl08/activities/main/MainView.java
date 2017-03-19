package com.slebbers.dunl08.activities.main;

/**
 * Created by Paul on 27/01/2017.
 */

public interface MainView  {
    void showChecklist(String equipmentID);
    void disableButtons();
    void equipmentNotFound();
    void toggleScanTagVisibility();
    void toggleProgressBarVisibility();
    void toggleSyncVisibility();
    void setPbProgress(int progress);
    void setSyncText(String text);
    void displayMessage(String message);
}