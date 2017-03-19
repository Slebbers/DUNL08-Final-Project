package com.slebbers.dunl08.activities.main;

import android.nfc.NdefRecord;

/**
 * Created by Paul on 27/01/2017.
 */

public interface Presenter {
    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
    void setupNFC();
    void onTagScanned(NdefRecord record);
    void onCheckboxClicked();
    void onSaveClicked();
    void onOptionsItemSelected(int id);
    void firstLaunch();
}