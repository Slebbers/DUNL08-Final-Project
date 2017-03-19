package com.slebbers.dunl08.activities.main;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.fragments.inspectionview.FragmentChecklistView;
import com.slebbers.dunl08.fragments.equipmentview.FragmentViewChecklists;

public class MainActivity extends AppCompatActivity implements MainView, NavigationView.OnNavigationItemSelectedListener {

    private Presenter mainPresenter;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private FragmentChecklistView fragmentChecklistView;
    private TextView tvScanTag;
    private TextView tvSync;
    private ProgressBar pbSync;
    private SharedPreferences sharedPrefs;
    private DrawerLayout drawer;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvSync = (TextView) findViewById(R.id.tvSync);
        pbSync = (ProgressBar) findViewById(R.id.pbSync);
        tvScanTag = (TextView) findViewById(R.id.tvScanTag);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("MainActivity", e.getMessage());
        }

        intents = new IntentFilter[] { ndef };
        technologies = new String[][] { new String[] { Ndef.class.getName() } };

        fragmentManager = getSupportFragmentManager();
        sharedPrefs = getSharedPreferences("com.slebbers.dunl08", MODE_PRIVATE);
        // REMOVE EVENTUALLY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

       // sharedPrefs.edit().putBoolean("initialLaunch", true).apply();

        if (mainPresenter == null)
            mainPresenter = new PresenterMain(this, new DatabaseAccessor(getApplicationContext()));

        mainPresenter.onCreate();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.onPause();

        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.onResume();

        // Should be checking if network connectivitity exists at this point
        if(sharedPrefs.getBoolean("initialLaunch", true)) {
            mainPresenter.firstLaunch();
            sharedPrefs.edit().putBoolean("initialLaunch", false).apply();
        }

        if (nfcAdapter != null)
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TODO: fix this not being removed by the fragment replace.
        // sorta fixed for now, some weird case were it stays visible
        tvScanTag.setVisibility(View.GONE);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage message = (NdefMessage) parcelables[0];
        NdefRecord record = message.getRecords()[0];
        mainPresenter.onTagScanned(record);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_view_inspection_details) {
            fragmentTransaction = fragmentManager.beginTransaction();
            FragmentViewChecklists fvc = new FragmentViewChecklists();
            fragmentTransaction.replace(R.id.content_main, fvc);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showChecklist(String equipmentID) {
        if(fragmentChecklistView == null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentChecklistView = new FragmentChecklistView();
            Bundle bundle = new Bundle();
            bundle.putString("MainActivity", equipmentID);
            fragmentChecklistView.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, fragmentChecklistView);
            fragmentTransaction.commit();
        } else {
            // Instead of recreating the whole fragment again, just update its contents
            fragmentChecklistView.reloadContents(equipmentID);
        }
    }

    @Override
    public void equipmentNotFound() {
        Toast.makeText(this, "Equipment does not exist in database.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void disableButtons() {

    }

    @Override
    public void toggleScanTagVisibility() {
        if(tvScanTag.getVisibility() == View.INVISIBLE)
            tvScanTag.setVisibility(View.VISIBLE);
        else
            tvScanTag.setVisibility(View.INVISIBLE);
    }

    @Override
    public void toggleProgressBarVisibility() {
        if(pbSync.getVisibility() == View.INVISIBLE)
            pbSync.setVisibility(View.VISIBLE);
        else
            pbSync.setVisibility(View.INVISIBLE);
    }

    @Override
    public void toggleSyncVisibility() {
        if(tvSync.getVisibility() == View.INVISIBLE)
            tvSync.setVisibility(View.VISIBLE);
        else
            tvSync.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSyncText(String text) {
        tvSync.setText(text);
    }

    @Override
    public void setPbProgress(int progress) {
        pbSync.setProgress(progress);
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
