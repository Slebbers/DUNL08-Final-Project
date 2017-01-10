package com.slebbers.dunl08.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.slebbers.dunl08.R;

/**
 * Created by Paul on 10/01/2017.
 */

public class FragmentDevice2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View device2 = inflater.inflate(R.layout.fragment_device2, container, false);
        Button submit = (Button) device2.findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(device2.getContext(), "Submitted equipment 2", Toast.LENGTH_LONG).show();
            }
        });

        return device2;
    }
}
