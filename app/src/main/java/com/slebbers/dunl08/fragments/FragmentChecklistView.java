package com.slebbers.dunl08.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.adapters.ChecklistAdapter;
import com.slebbers.dunl08.database.ChecklistDataContract;
import com.slebbers.dunl08.database.ChecklistDataDbHelper;


import java.util.ArrayList;
import java.util.List;

public class FragmentChecklistView extends Fragment {

    private TextView tvEquipmentID;
    private TextView tvLastInspection;
    private TextView tvNextInspection;
    private TextView tvStatus;

    private RelativeLayout rlChecklist;
    private RecyclerView rvChecklist;

    private ChecklistDataDbHelper dbHelper;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checklist_view, container, false);

        // TextViews
        tvEquipmentID = (TextView) view.findViewById(R.id.tvEquipmentID);
        tvLastInspection = (TextView) view.findViewById(R.id.tvLastInspection);
        tvNextInspection = (TextView) view.findViewById(R.id.tvNextInspection);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        rvChecklist = (RecyclerView) view.findViewById(R.id.rvChecklist);

        dbHelper =  new ChecklistDataDbHelper(getContext());

        // TODO: Move all this from onCreateView (do this somewhere more efficient)
        String equipmentID = getArguments().getString("MainActivity");

        tvEquipmentID.setText(equipmentID);
        db = dbHelper.getReadableDatabase();

        String query = "SELECT ChecklistItem FROM ChecklistItem WHERE ChecklistID = (SELECT ChecklistID FROM Equipment WHERE EquipmentID = " + equipmentID + ")";

        Cursor cursor = db.rawQuery(query, null);
        List<String> items = new ArrayList<>();

        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM));
            Log.d("ChecklistView", item);
            items.add(item);
        }

        Log.d("ChecklistView", Integer.toString(items.size()));
        cursor.close();

        for(String s : items) {
            Log.d("ChecklistView", s);
        }

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
        }

        statusCursor.close();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChecklist.setLayoutManager(layoutManager);
        ChecklistAdapter adapter = new ChecklistAdapter(items);
        rvChecklist.setAdapter(adapter);




        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


    private List<String> getChecklistContents(String equipmentID) {
        // I.E:
        // SELECT * FROM CHECKLISTITEM WHERE CHECKLISTID =
        // (SELECT CHECKLISTID FROM EQUIPMENT WHERE EQUIPMENTID = (scanned tag id));



//        String[] projection = {
//                ChecklistDataContract.ChecklistItemEntry.COLUMN_CHECKLISTITEM
//        };
//
//        Cursor cursor = db.query(
//                ChecklistDataContract.ChecklistItemEntry.TABLE_NAME, // table
//                projection,                                          // columns returned
//
//
//        )

        return new ArrayList<>();

    }
}
