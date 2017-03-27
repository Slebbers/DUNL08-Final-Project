package com.slebbers.dunl08.activities.main;

import android.nfc.NdefRecord;

/**
 *  Interface for the Presenter for the MainView.
 */

public interface Presenter {
    /**
     * Represents the onCreate part of the Activity lifecycle
     */
    void onCreate();
    /**
     * Represents the onPause part of the Activity lifecycle
     */
    void onPause();
    /**
     * Represents the onResume part of the Activity lifecycle
     */
    void onResume();
    /**
     * Represents the onDestroy part of the Activity lifecycle
     */
    void onDestroy();
    /**
     * Processes the record from the scanned NFC tag
     * @param record The record recieved from the tag
     */
    void onTagScanned(NdefRecord record);
    /**
     * Handles user interaction with the Options Menu
     * @param id The element selected
     */
    void onOptionsItemSelected(int id);
    /**
     * Handles the applications initial startup
     */
    void firstLaunch();
}