package com.slebbers.dunl08.fragments.inspectionview;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.slebbers.dunl08.R;
import com.slebbers.dunl08.model.ChecklistItem;

import java.util.List;

/**
 * This fragment is used to display Checklist data on screen.
 */
public class FragmentChecklistView extends Fragment implements ChecklistView {

    // View Elements
    private TextView tvEquipmentType;
    private TextView tvLastInspection;
    private TextView tvNextInspection;
    private TextView tvStatus;
    private Button btnSubmit;
    private Button btnReinspect;
    private RecyclerView rvChecklist;
    private RecyclerView.LayoutManager layoutManager;
    private ChecklistAdapter checklistAdapter;
    private ChecklistViewPresenter presenter;

    // Safety phrases
    private final String GOOD_TO_GO = "Good To Go";
    private final String DO_NOT_USE = "Do Not Use";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checklist_view, container, false);
        tvEquipmentType = (TextView) view.findViewById(R.id.tvEquipmentType);
        tvLastInspection = (TextView) view.findViewById(R.id.tvLastInspection);
        tvNextInspection = (TextView) view.findViewById(R.id.tvNextInspection);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setEnabled(false);
        btnReinspect = (Button) view.findViewById(R.id.btnReinspect);
        rvChecklist = (RecyclerView) view.findViewById(R.id.rvChecklist);
        presenter = new PresenterChecklistView(getContext(), this);
        presenter.setEquipmentID(getArguments().getString("MainActivity"));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();

        btnReinspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnReinspectClick();
                btnSubmit.setEnabled(true);
                btnReinspect.setEnabled(false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setEnabled(false);
                presenter.btnSubmitClick(isNetworkConnected());
            }
        });
    }

    /**
     * Checks to see if the device is connected to a network
     * @return {@code True if connected } or {@code False if no network is available/}
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void displayEquipmentType(String equipmentType) {
        tvEquipmentType.setText(equipmentType);
    }

    @Override
    public void displayNextInspection(String date) {
        if(date != null) {
            if (date.isEmpty() || date.equals("null"))
                tvNextInspection.setText("Not Set");
            else
                tvNextInspection.setText(date);
        } else {
            tvNextInspection.setText("Not Set");
        }
    }

    @Override
    public void displayLastInspection(String date) {
        if(date != null) {
            if (date.isEmpty() || date.equals("null"))
                tvLastInspection.setText("Not Set");
            else
                tvLastInspection.setText(date);
        } else {
            tvLastInspection.setText("Not Set");
        }

    }

    @Override
    public void displayStatus(String status) {
        if(status != null) {
            if(status.isEmpty() || status.equals("null")) {
                tvStatus.setText("Not Set");
            } else {
                if(status.equals(GOOD_TO_GO))
                    tvStatus.setTextColor(Color.GREEN);
                else if(status.equals(DO_NOT_USE))
                    tvStatus.setTextColor(Color.RED);

                tvStatus.setText(status);
            }
        } else {
            tvStatus.setText("Not Set");
        }
    }

    @Override
    public void displayChecklistItems(List<ChecklistItem> checklistItems) {
        layoutManager = new LinearLayoutManager(getContext());
        rvChecklist.setLayoutManager(layoutManager);
        checklistAdapter = new ChecklistAdapter(checklistItems);
        rvChecklist.setAdapter(checklistAdapter);
    }

    @Override
    public void disableButtons() {
        btnSubmit.setEnabled(false);
        btnReinspect.setEnabled(true);
    }

    @Override
    public List<ChecklistItem> getCheckboxes() {
        return checklistAdapter.getCheckboxes();
    }


    public void reloadContents(String equipmentID) {
        presenter.setEquipmentID(equipmentID);
        presenter.onStart();
        btnReinspect.setEnabled(true);
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}