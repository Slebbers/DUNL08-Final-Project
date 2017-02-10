package com.slebbers.dunl08.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.slebbers.dunl08.R;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<String> items;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.cbItem);
        }
    }

    public ChecklistAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkbox.setText(items.get(position));
    }
}
