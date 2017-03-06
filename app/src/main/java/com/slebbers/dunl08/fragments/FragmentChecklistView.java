package com.slebbers.dunl08.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.slebbers.dunl08.R;
import com.slebbers.dunl08.adapters.ChecklistAdapter;
import com.slebbers.dunl08.interfaces.ChecklistView;
import com.slebbers.dunl08.presenters.PresenterChecklistView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentChecklistView extends Fragment implements ChecklistView {

    private TextView tvEquipmentType;
    private TextView tvLastInspection;
    private TextView tvNextInspection;
    private TextView tvStatus;
    private Button btnSubmit;
    private Button btnReinspect;
    private RecyclerView rvChecklist;
    private RecyclerView.LayoutManager layoutManager;
    private ChecklistAdapter checklistAdapter;
    private PresenterChecklistView presenter;

    private final String GOOD_TO_GO = "Good To Go";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checklist_view, container, false);
        tvEquipmentType = (TextView) view.findViewById(R.id.tvEquipmentType);
        tvLastInspection = (TextView) view.findViewById(R.id.tvLastInspection);
        tvNextInspection = (TextView) view.findViewById(R.id.tvNextInspection);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
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
                checklistAdapter.clearCheckboxes();
                presenter.onStart();
                btnSubmit.setEnabled(true);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnSubmitClick();
                Toast.makeText(getContext(), "Checklist saved, rescan to verify!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void displayEquipmentType(String equipmentType) {
        tvEquipmentType.setText(equipmentType);
    }

    @Override
    public void displayNextInspection(String date) {
        tvNextInspection.setText(date);
    }

    @Override
    public void displayLastInspection(String date) {
        tvLastInspection.setText(date);
    }

    @Override
    public void displayStatus(String status) {
        if(status != null) {
            if(status.equals(GOOD_TO_GO))
                tvStatus.setTextColor(Color.GREEN);
            else
                tvStatus.setTextColor(Color.RED);
        }

        tvStatus.setText(status);
    }

    @Override
    public void displayChecklistItems(HashMap<String, Integer> checklistItems) {
        layoutManager = new LinearLayoutManager(getContext());
        rvChecklist.setLayoutManager(layoutManager);

        List<String> checklistNames = new ArrayList<>();
        for(Map.Entry entry : checklistItems.entrySet()) {
            Log.d("checklist", entry.getKey().toString());
            checklistNames.add(entry.getKey().toString());
        }

        checklistAdapter = new ChecklistAdapter(checklistItems, checklistNames);
        rvChecklist.setAdapter(checklistAdapter);
    }

    @Override
    public void disableButtons() {
        btnSubmit.setEnabled(false);
    }

    @Override
    public List<CheckBox> getCheckboxes() {
        return checklistAdapter.getCheckboxes();
    }


    public void reloadContents(String equipmentID) {
        presenter.setEquipmentID(equipmentID);
        presenter.onStart();
    }
}
