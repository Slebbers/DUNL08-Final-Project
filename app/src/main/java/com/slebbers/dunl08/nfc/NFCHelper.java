package com.slebbers.dunl08.nfc;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by Paul on 20/01/2017.
 */

public class NFCHelper {
    private Ndef ndefTag;

    public NFCHelper() {

    }

    // probably should be boolean.
    public void writeTag(Tag tag, String tagText) {
        ndefTag = Ndef.get(tag);

        try {
            // new tag not formatted
            if(ndefTag == null) {
                NdefFormatable form = NdefFormatable.get(tag);
                if(form == null) {
                    form.connect();
                    form.format(buildMessage(tagText));
                    form.close();
                }
            } else {
                ndefTag.connect();
                ndefTag.writeNdefMessage(buildMessage(tagText));
                ndefTag.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    // return some kind of data here
    public void readTag() {

    }

    public NdefMessage buildMessage(String text) {

        try {
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] textPayload = text.getBytes("UTF-8");

            int langSize = lang.length;
            int textLength = textPayload.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(textPayload, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT, new byte[0],
                    payload.toByteArray());
            return new NdefMessage(new NdefRecord[] {record});
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
