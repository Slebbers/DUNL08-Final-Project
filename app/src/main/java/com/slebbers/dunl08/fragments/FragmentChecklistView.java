package com.slebbers.dunl08.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


import com.slebbers.dunl08.R;
import com.slebbers.dunl08.adapters.ChecklistAdapter;
import com.slebbers.dunl08.database.ChecklistDataContract;
import com.slebbers.dunl08.database.ChecklistDataDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class FragmentChecklistView extends Fragment {

    private TextView tvEquipmentID;
    private TextView tvLastInspection;
    private TextView tvNextInspection;
    private TextView tvStatus;
    private Button btnSubmit;
    private Button btnReinspect;
    private RecyclerView rvChecklist;

    private ChecklistDataDbHelper dbHelper;
    private ChecklistAdapter adapter;
    private SQLiteDatabase db;

    String equipmentID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checklist_view, container, false);
        tvEquipmentID = (TextView) view.findViewById(R.id.tvEquipmentID);
        tvLastInspection = (TextView) view.findViewById(R.id.tvLastInspection);
        tvNextInspection = (TextView) view.findViewById(R.id.tvNextInspection);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnReinspect = (Button) view.findViewById(R.id.btnReinspect);
        rvChecklist = (RecyclerView) view.findViewById(R.id.rvChecklist);
        dbHelper =  new ChecklistDataDbHelper(getContext());
        equipmentID = getArguments().getString("MainActivity");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        tvEquipmentID.setText(equipmentID);
        db = dbHelper.getReadableDatabase();

        btnReinspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reinspect();
                btnSubmit.setEnabled(true);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CheckBox> checkboxes = adapter.getCheckboxes();
                List<Integer> isChecked = new ArrayList<>();

                for(CheckBox cb : checkboxes) {
                    if(cb.isChecked()) {
                        isChecked.add(1);
                    } else {
                        isChecked.add(0);
                    }

                }

                String query = "UPDATE ChecklistItem SET IsChecked = ";
                int checkedCount = 0;
                Cursor cursor;

                for(int i = 0; i < checkboxes.size(); i++) {
                    String actualQuery = query + isChecked.get(i).toString() + " WHERE ChecklistID = " + equipmentID;
                    cursor = db.rawQuery(actualQuery, null);
                    cursor.moveToFirst();
                    cursor.close();
                    checkedCount++;
                }


                DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                Calendar c = Calendar.getInstance();
                Date date = new Date();
                c.setTime(date);

                String query2 = "UPDATE Equipment SET LastInspection = "  +  "'" + dateFormat.format(date) +  "'" + " WHERE EquipmentID = " + equipmentID;
                cursor = db.rawQuery(query2, null);
                cursor.moveToFirst();
                cursor.close();

                c.add(Calendar.MONTH, 1);
                date = c.getTime();

                String query3 = "UPDATE Equipment SET NextInspection = " +  "'" + dateFormat.format(date) + "'" + " WHERE EquipmentID = " + equipmentID;
                cursor = db.rawQuery(query3, null);
                cursor.moveToFirst();
                cursor.close();

                String query4 = "UPDATE Equipment SET Status = ";

                if(checkedCount == checkboxes.size()) {
                    query4 += "'Good To Go'";

                } else {
                    query4 += "'Do Not Use'";
                }

                query4 += " WHERE EquipmentID = " + equipmentID;
                cursor = db.rawQuery(query4, null);
                cursor.moveToFirst();
                cursor.close();

            }
        });
    }

    private void reinspect() {
        String query = "UPDATE Equipment SET NextInspection = NULL, Status = NULL WHERE EquipmentID = " + equipmentID;
        String query2 = "UPDATE ChecklistItem SET IsChecked = 0 WHERE ChecklistID = " + equipmentID;
       // db = dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        // needed otherwise tables wont update
        cursor.moveToFirst();
        cursor.close();

        cursor = db.rawQuery(query2, null);
        cursor.moveToFirst();
        cursor.close();

        Log.d("ChecklistView", "reinspecting");
        reloadContents(equipmentID);
    }

    private void getDataFromDatabase() {
        String query = "SELECT ChecklistItem, IsChecked FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";
        Cursor cursor = db.rawQuery(query, null);
        List<String> items = new ArrayList<>();
        List<Integer> checkStatus = new ArrayList<>();

        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ChecklistDataContract.ChecklistItemEntry.COLUMN_ISCHECKED));
            //Log.d("ChecklistView", item);
            items.add(item);
            checkStatus.add(isChecked);
        }


        Log.d("ChecklistView", Integer.toString(items.size()));
        cursor.close();



//        for(String s : items) {
//            Log.d("ChecklistView", s);
//        }

        // EquipmentID will not always equal ChecklistID, testing for now.
        String lastInspectionQuery = "SELECT LastInspection FROM Equipment WHERE ChecklistID = " + equipmentID;
        String nextInspectionQuery = "SELECT NextInspection FROM Equipment WHERE ChecklistID = " + equipmentID;
        String statusQuery = "SELECT Status FROM Equipment WHERE ChecklistID = " + equipmentID;

        Cursor lastInspectionCursor = db.rawQuery(lastInspectionQuery, null);
        if(lastInspectionCursor.moveToFirst()) {
            String lastInspection = lastInspectionCursor.getString(0);
            tvLastInspection.setText(lastInspection);
        }


        lastInspectionCursor.close();

        Cursor nextInspectionCursor = db.rawQuery(nextInspectionQuery, null);
        if(nextInspectionCursor.moveToFirst()) {
            String nextInspection = nextInspectionCursor.getString(0);
            tvNextInspection.setText(nextInspection);
        }
        nextInspectionCursor.close();

        Cursor statusCursor = db.rawQuery(statusQuery, null);
        if(statusCursor.moveToFirst()) {
            String status = statusCursor.getString(0);
            tvStatus.setText(status);

            if(status != null) {
                if(status.equals("Good To Go")) {
                    tvStatus.setTextColor(Color.GREEN);
                } else {
                    tvStatus.setTextColor(Color.RED);
                }
            }


        }

        statusCursor.close();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChecklist.setLayoutManager(layoutManager);
        adapter = new ChecklistAdapter(items);
        adapter.setChecked(checkStatus);
        rvChecklist.setAdapter(adapter);

        if(tvStatus.getText().equals("Good To Go")) {
            btnSubmit.setEnabled(false);
        }

    }

    private void setupViewContents() {

    }

    public void reloadContents(String equipmentID) {
        this.equipmentID = equipmentID;
        tvEquipmentID.setText(equipmentID);
        getDataFromDatabase();
    }


}
