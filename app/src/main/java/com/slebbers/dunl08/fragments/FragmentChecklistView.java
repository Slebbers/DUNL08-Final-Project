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
import com.slebbers.dunl08.model.ChecklistItem;
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
                Toast.makeText(getContext(), "Checklist saved, saving to server...", Toast.LENGTH_SHORT).show();
                presenter.btnSubmitClick();

                //TODO: make checklistitems disabled again
            }
        });
    }

    @Override
    public void displayEquipmentType(String equipmentType) {
        tvEquipmentType.setText(equipmentType);
    }

    @Override
    public void displayNextInspection(String date) {
        if(date != null) {
            if (date.isEmpty())
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
            if (date.isEmpty())
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
            if(status.isEmpty()) {
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
    }

    @Override
    public List<ChecklistItem> getCheckboxes() {
        return checklistAdapter.getCheckboxes();
    }


    public void reloadContents(String equipmentID) {
        presenter.setEquipmentID(equipmentID);
        presenter.onStart();
    }
}
