package com.slebbers.dunl08.fragments.equipmentview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.activities.writetag.WriteTagActivity;
import com.slebbers.dunl08.model.Checklist;

import java.util.List;

/**
 * Adapter class that deals with constructing cards within a RecyclerView
 */
public class ChecklistCardViewAdapter extends RecyclerView.Adapter<ChecklistCardViewAdapter.ViewHolder> {

    private List<Checklist> checklists;
    private String GOOD_TO_GO = "Good To Go";
    private String DO_NOT_USE = "Do Not Use";

    /**
     * Custom ViewHolder object that will store an individual card
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tvEquipmentType;
        TextView tvLastInspection;
        TextView tvNextInspection;
        TextView tvStatus;
        Checklist currentChecklist;

        public ViewHolder(View cardView) {
            super(cardView);
            view = cardView;
            tvEquipmentType = (TextView) cardView.findViewById(R.id.tvEquipmentType);
            tvLastInspection = (TextView) cardView.findViewById(R.id.tvLastInspection);
            tvNextInspection = (TextView) cardView.findViewById(R.id.tvNextInspection);
            tvStatus = (TextView) cardView.findViewById(R.id.tvStatus);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Launch our activity to write the tag
                    Intent intent = new Intent(view.getContext(), WriteTagActivity.class);
                    intent.putExtra("EquipmentID", currentChecklist.getEquipmentID());
                    intent.putExtra("EquipmentName", currentChecklist.getEquipmentName());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    /**
     * Constructs a new ChecklistCardViewAdapter
     * @param checklists Checklists to be displayed as cards
     */
    public ChecklistCardViewAdapter(List<Checklist> checklists) {
        this.checklists = checklists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklists_card_view, parent, false);
        return new ChecklistCardViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.currentChecklist = checklists.get(position);
        holder.tvEquipmentType.setText(checklists.get(position).getEquipmentName());

        // Null database cursors set string values to "null" ...
        if(checklists.get(position).getLastInspection().equals("null")) {
            holder.tvLastInspection.setText("Not Set");
        } else {
            holder.tvLastInspection.setText(checklists.get(position).getLastInspection());
        }

        if(checklists.get(position).getNextInspection().equals("null")) {
            holder.tvNextInspection.setText("Not Set");
        } else {
            holder.tvNextInspection.setText(checklists.get(position).getNextInspection());
        }

        if(checklists.get(position).getStatus().equals("null")) {
            holder.tvStatus.setText("Not Set");
        } else {
            holder.tvStatus.setText(checklists.get(position).getStatus());
            if(holder.tvStatus.getText().toString().equals(GOOD_TO_GO)) {
                holder.tvStatus.setTextColor(Color.GREEN);
            } else if(holder.tvStatus.getText().toString().equals(DO_NOT_USE)){
                holder.tvStatus.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return checklists.size();
    }
}