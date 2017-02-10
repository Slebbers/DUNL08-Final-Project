package com.slebbers.dunl08.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.slebbers.dunl08.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 10/01/2017.
 */

public class FragmentDevice2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View device2 = inflater.inflate(R.layout.fragment_device2, container, false);
        final Button submit = (Button) device2.findViewById(R.id.btnSubmit);
        final TextView tvEquipmentName = (TextView) device2.findViewById(R.id.tvEquipName);
        final TextView tvStatus = (TextView) device2.findViewById((R.id.tv));
        final CheckBox checkBox = (CheckBox) device2.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = (CheckBox) device2.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox) device2.findViewById(R.id.checkBox5);
        final CheckBox checkBox4 = (CheckBox) device2.findViewById(R.id.checkBox4);

        final List<CheckBox> checkboxes = new ArrayList<>();
        Context context = getActivity();
        final SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key2), Context.MODE_PRIVATE);

        Log.d("Test", "restarting...");
        checkboxes.add(checkBox);
        checkboxes.add(checkBox2);
        checkboxes.add(checkBox3);
        checkboxes.add(checkBox4);

        tvEquipmentName.setText("device2");

        // check to see if we've saved this tag before
        Map<String, ?> savedPrefs = sharedPreferences.getAll();

        if(!savedPrefs.isEmpty()) {
            for(Map.Entry<String, ?> entry : savedPrefs.entrySet()) {
                Log.d("values", entry.getKey() + " " + entry.getValue());
                for(int i = 0; i < checkboxes.size(); i++) {
                    if(entry.getKey().equals(Integer.toString(checkboxes.get(i).getId()))) {
                        if(entry.getValue().equals(true)) {
                            checkboxes.get(i).setChecked(true);
                        }
                        checkboxes.get(i).setEnabled(false);
                    }
                }
            }

            if(checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                tvStatus.setText("GOOD TO GO");
                tvStatus.setTextColor(Color.GREEN);
            }

            submit.setEnabled(false);
        } else {
            for(int i = 0; i < checkboxes.size(); i++) {
                checkboxes.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkBox.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                            tvStatus.setText("GOOD TO GO");
                            tvStatus.setTextColor(Color.GREEN);
                        } else {
                            tvStatus.setText("DO NOT USE");
                            tvStatus.setTextColor(Color.RED);
                        }
                    }
                });
            }

            final SharedPreferences.Editor editor = sharedPreferences.edit();

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(device2.getContext(), "Saved equipment 2", Toast.LENGTH_LONG).show();
                    // save to shared prefs
                    for(CheckBox c : checkboxes) {
                        if(c.isChecked()) {
                            editor.putBoolean(Integer.toString(c.getId()),true);
                        } else {
                            editor.putBoolean(Integer.toString(c.getId()),false);
                        }
                    }

                    editor.commit();
                    submit.setEnabled(false);
                    checkBox.setEnabled(false);
                    checkBox2.setEnabled(false);
                    checkBox3.setEnabled(false);
                    checkBox4.setEnabled(false);
                }
            });
        }

        return device2;
    }
}
