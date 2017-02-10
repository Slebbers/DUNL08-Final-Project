package com.slebbers.dunl08.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.fragments.FragmentChecklistView;
import com.slebbers.dunl08.fragments.FragmentDevice1;
import com.slebbers.dunl08.fragments.FragmentDevice2;
import com.slebbers.dunl08.fragments.FragmentDevice3;
import com.slebbers.dunl08.interfaces.MainView;
import com.slebbers.dunl08.nfc.NFCHelper;
import com.slebbers.dunl08.presenters.PresenterMain;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements MainView {

    private PresenterMain mainPresenter;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private TextView tvScanTag;

    // NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter ndef;
    private IntentFilter[] intents;
    private String[][] technologies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mainPresenter == null)
            mainPresenter = new PresenterMain(getApplicationContext(), this);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("MainActivity", e.getMessage());
        }

        intents = new IntentFilter[]{ ndef };
        technologies = new String[][]{ new String[] { Ndef.class.getName() } };


        mainPresenter.onCreate();
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.onPause();

        if(nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.onResume();

        if(nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intents, technologies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mainPresenter.onOptionsItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showChecklist() {

    }

    @Override
    public void disableButtons() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mainPresenter.onTagScanned(intent);

        // TODO: fix this not being removed by the fragment replace.
        tvScanTag = (TextView) findViewById(R.id.tvScanTag);
        tvScanTag.setVisibility(View.GONE);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage message = (NdefMessage) parcelables[0];
        NdefRecord[] records = message.getRecords();

        // for now, only read the first record
        NdefRecord record = records[0];
        try {
            byte[] content = record.getPayload();
            String encoding = ((content[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languages = content[0] & 0063;
            String text = new String(content, languages + 1, content.length - languages - 1, encoding);
            Toast.makeText(this, "Tag scanned, ID = " + text, Toast.LENGTH_LONG).show();

            fragmentTransaction = fragmentManager.beginTransaction();
            Log.d("mainactivity", text);

            Bundle bundle = new Bundle();
            bundle.putString("MainActivity", text);

            // TODO: Don't recreate this fragment every time
            FragmentChecklistView fragmentChecklistView = new FragmentChecklistView();
            fragmentChecklistView.setArguments(bundle);

            fragmentTransaction.replace(R.id.content_main, fragmentChecklistView);

            fragmentTransaction.commit();
        } catch (UnsupportedEncodingException e) {
            Log.e("MainActivity", e.getMessage());
        }
    }


//    @Override
//    public void clearPrefs() {
//        // TODO: remove sharedprefs when database is active
//        getSharedPreferences("device1", 0).edit().clear().apply();
//        getSharedPreferences("device2", 0).edit().clear().apply();
//        getSharedPreferences("device3", 0).edit().clear().apply();
//        Toast.makeText(this, "Prefs cleared!", Toast.LENGTH_LONG).show();
//    }
}
