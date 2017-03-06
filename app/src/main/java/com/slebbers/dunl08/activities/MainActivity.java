package com.slebbers.dunl08.activities;

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
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.fragments.FragmentChecklistView;
import com.slebbers.dunl08.fragments.FragmentViewChecklists;
import com.slebbers.dunl08.interfaces.MainView;
import com.slebbers.dunl08.network.ServerConnect;
import com.slebbers.dunl08.presenters.PresenterMain;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MainView, NavigationView.OnNavigationItemSelectedListener {

    private PresenterMain mainPresenter;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private FragmentChecklistView fragmentChecklistView;
    private TextView tvScanTag;
    private DatabaseAccessor dbAccessor;
    private SharedPreferences sharedPrefs;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbAccessor = new DatabaseAccessor(getApplicationContext());

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
        technologies = new String[][]{new String[]{Ndef.class.getName()}};

        mainPresenter.onCreate();
        fragmentManager = getSupportFragmentManager();

        sharedPrefs = getSharedPreferences("com.slebbers.dunl08", MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if(sharedPrefs.getBoolean("initialLaunch", true)) {
            mainPresenter.syncDatabase();
            Toast.makeText(this, "First launch. Syncing database....", Toast.LENGTH_LONG).show();
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
        // sorta fixed for now, some weird case were it stays visible
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
            Toast.makeText(this, "Tag scanned, ID = " + text, Toast.LENGTH_SHORT).show();


            Log.d("mainactivity", text);

            Bundle bundle = new Bundle();
            bundle.putString("MainActivity", text);

            // we need to make sure the scanned tag exists before building the fragment, to avoid crashes
            // when the id is not in the database.
            //dbAccessor.setReadMode();

            if (dbAccessor.checkEquipmentExists(text)) {
                if (fragmentChecklistView == null) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentChecklistView = new FragmentChecklistView();
                    fragmentChecklistView.setArguments(bundle);
                    fragmentTransaction.replace(R.id.content_main, fragmentChecklistView);
                    fragmentTransaction.commit();
                } else {
                    //update contents rather than replace view
                    fragmentChecklistView.reloadContents(text);
                }
            } else {
                Toast.makeText(this, "Equipment not found in database!", Toast.LENGTH_LONG).show();
                if (fragmentChecklistView == null) {
                    tvScanTag.setVisibility(View.VISIBLE);
                }

            }
        } catch (UnsupportedEncodingException e) {
            Log.e("MainActivity", e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_write_tag) {
            // otherwise the main activity will open when we attempt to write to the tag
            nfcAdapter.disableForegroundDispatch(this);
            Intent intent = new Intent(this, WriteTagActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_view_inspection_details) {
            fragmentTransaction = fragmentManager.beginTransaction();
            FragmentViewChecklists fvc = new FragmentViewChecklists();
            fragmentTransaction.replace(R.id.content_main, fvc);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
