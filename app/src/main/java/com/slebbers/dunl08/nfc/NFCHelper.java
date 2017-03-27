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
 * Class that provides functionality for reading and writing data from NFC tags
 */

public class NFCHelper {

    private Ndef ndefTag;

    /**
     * Constructs a new instance of NFCHelper
     */
    public NFCHelper() {

    }

    /**
     * Writes a string of text to a tag
     * @param tag The tag to be written to
     * @param tagText The text to be written
     */
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

    /**
     * Reads data from an NFC Tag according the specification of plain text
     * @param record The NdefRecord to be read
     * @return String containing the value on the Tag
     * @throws UnsupportedEncodingException Tag is not in a valid format
     */
    public String readTag(NdefRecord record) throws UnsupportedEncodingException {
        byte[] content = record.getPayload();
        String encoding = ((content[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languages = content[0] & 0063;
        return new String(content, languages + 1, content.length - languages - 1, encoding);
    }

    /**
     * Constructs an NDEF message for plain text according the the NFC specification
     * @param text The text to be placed inside the message
     * @return The constructed NdefMessage
     */
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
