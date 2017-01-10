package com.slebbers.dunl08.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.fragments.FragmentDevice1;
import com.slebbers.dunl08.fragments.FragmentDevice2;
import com.slebbers.dunl08.fragments.FragmentDevice3;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intents;
    private String[][] technologies;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        fragmentManager = getSupportFragmentManager();
        /* This allows this activity to recieve nfc intents (which would otherwise launch
        *  the application */
       pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("mainactivity", e.getMessage());
        }

        intents = new IntentFilter[] {ndef };
        technologies = new String[][] { new String[] { Ndef.class.getName() } };
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intents, technologies);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       // Tag scannedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

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
            //Toast.makeText(this, text, Toast.LENGTH_LONG).show();

            fragmentTransaction = fragmentManager.beginTransaction();
            Log.d("mainactivity", text);

            // Simulating each tag being a new device with a unique checklist,
            // so display a new fragment per tag.
            switch(text) {
                case "device1":
                    fragmentTransaction.replace(R.id.content_main, new FragmentDevice1());
                    break;
                case "device2":
                    fragmentTransaction.replace(R.id.content_main, new FragmentDevice2());
                    break;
                case "device3":
                    fragmentTransaction.replace(R.id.content_main, new FragmentDevice3());
                    break;

                default: break;
            }

            fragmentTransaction.commit();
        } catch(UnsupportedEncodingException e) {
            Log.e("mainacitivity", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
