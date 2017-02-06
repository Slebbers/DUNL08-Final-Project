package com.slebbers.dunl08.interfaces;

import android.content.Intent;

/**
 * Created by Paul on 27/01/2017.
 */

public interface Presenter {
    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
    void setupNFC();
    void onTagScanned(Intent intent);
    void onCheckboxClicked();
    void onSaveClicked();
    void onOptionsItemSelected(int id);
    void bindView(MainView v);
}
