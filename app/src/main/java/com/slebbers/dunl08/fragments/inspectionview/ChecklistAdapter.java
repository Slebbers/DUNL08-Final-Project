package com.slebbers.dunl08.fragments.inspectionview;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.slebbers.dunl08.R;
import com.slebbers.dunl08.model.ChecklistItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class that is used to display checklists in a RecyclerView
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<CheckBox> checkboxes;
    private List<ChecklistItem> checklistItems;

    /**
     * Custom ViewHolder to display each ChecklistItem with a Checkbox
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.cbItem);
        }
    }

    /**
     * Constructs a new ChecklistAdapter
     * @param checklistItems Items to be displayed within the Checklist
     */
    public ChecklistAdapter(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
        checkboxes = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChecklistItem item = checklistItems.get(position);

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setText(item.getChecklistItem());


        if(item.getIsChecked().equals("1")) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        if(item.getIsEnabled())
            holder.checkbox.setEnabled(true);
        else
            holder.checkbox.setEnabled(false);

        if(!checkboxes.contains(holder.checkbox))
            checkboxes.add(holder.checkbox);

        // If our recyclerview scrolls, we need to be able to save if the checkbox was checked or not.
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    checklistItems.get(holder.getAdapterPosition()).setIsChecked("1");
                } else {
                    checklistItems.get(holder.getAdapterPosition()).setIsChecked("0");
                }
            }
        });
    }

    public List<ChecklistItem> getCheckboxes() {
        return checklistItems;
    }
}
