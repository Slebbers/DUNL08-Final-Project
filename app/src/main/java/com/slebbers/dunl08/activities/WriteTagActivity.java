package com.slebbers.dunl08.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.nfc.NFCHelper;

public class WriteTagActivity extends AppCompatActivity {

    private  NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private String tagWriteableName;
    private AlertDialog writeTagDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        Button button = (Button) findViewById(R.id.btnWrite);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText tagID = (EditText) findViewById(R.id.etTagName);
                tagWriteableName = tagID.getText().toString();

                writeTagDialog = new AlertDialog.Builder(WriteTagActivity.this)
                        .setTitle("Write to tag")
                        .setMessage("Place the device on the tag to write data.")
                        .create();

                writeTagDialog.show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

            }
        });


    }



    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NFCHelper nfcHelper = new NFCHelper();
        Log.d("WriteTagActivity", "writing tag ...");
        writeTagDialog.setMessage("Writing tag...");
        nfcHelper.writeTag(tag, tagWriteableName);
        writeTagDialog.setMessage("Tag successfully written!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] writeTagFilters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    @Override
    protected void onPause() {

        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }


}
