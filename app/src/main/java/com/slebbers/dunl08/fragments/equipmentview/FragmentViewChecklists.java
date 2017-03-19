package com.slebbers.dunl08.fragments.equipmentview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.database.DatabaseAccessor;
import com.slebbers.dunl08.model.Checklist;

import java.util.List;

public class FragmentViewChecklists extends Fragment {

    private RecyclerView rvChecklists;
    private DatabaseAccessor db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_checklists, container, false);
        db = new DatabaseAccessor(getContext());
        rvChecklists = (RecyclerView) view.findViewById(R.id.rvCardView);
        rvChecklists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChecklists.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Checklist> checklists = db.getCardData();
        ChecklistCardViewAdapter adapter = new ChecklistCardViewAdapter(checklists);
        rvChecklists.setAdapter(adapter);
    }
}
