package com.slebbers.dunl08.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.slebbers.dunl08.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<String> items;
    private HashMap<String, Integer> checklistItems;
    private static List<CheckBox> checkboxes = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.cbItem);
        }

    }

    public ChecklistAdapter(HashMap<String, Integer> checklistItems, List<String> items) {
        this.checklistItems = checklistItems;
        this.items = items;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkbox.setText(items.get(position));

        if(checklistItems.get(items.get(position)) == 1) {
            holder.checkbox.setChecked(true);
            holder.checkbox.setEnabled(false);
        } else {
            holder.checkbox.setChecked(false);
            holder.checkbox.setEnabled(true);
        }

        if(!checkboxes.contains(holder.checkbox))
            checkboxes.add(holder.checkbox);


    }

    public List<CheckBox> getCheckboxes() {
       // return checkboxes;
        return checkboxes;
    }

    public void clearCheckboxes() {
        checkboxes.clear();
    }


}
