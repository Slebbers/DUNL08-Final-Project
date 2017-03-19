package com.slebbers.dunl08.model;

import java.util.ArrayList;
import java.util.List;

public class Checklist {

    private String ChecklistID;
    private String EquipmentID;
    private String EquipmentName;
    private String LastInspection;
    private String NextInspection;
    private String Status;
    private List<ChecklistItem> ChecklistItems;

    public Checklist() {
        ChecklistItems = new ArrayList<>();
    }

    public String getChecklistID() {
        return ChecklistID;
    }

    public void setChecklistID(String checklistID) {
        ChecklistID = checklistID;
    }

    public String getEquipmentID() {
        return EquipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        EquipmentID = equipmentID;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getLastInspection() {
        return LastInspection;
    }

    public void setLastInspection(String lastInspection) {
        LastInspection = lastInspection;
    }

    public String getNextInspection() {
        return NextInspection;
    }

    public void setNextInspection(String nextInspection) {
        NextInspection = nextInspection;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<ChecklistItem> getChecklistItems() {
        return ChecklistItems;
    }

    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        ChecklistItems = checklistItems;
    }

    public void addChecklistItem(ChecklistItem item) {
        ChecklistItems.add(item);
    }
}
