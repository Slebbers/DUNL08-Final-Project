package com.slebbers.dunl08.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class that stores information related to Checklists
 */
public class Checklist {

    private String ChecklistID;
    private String EquipmentID;
    private String EquipmentName;
    private String LastInspection;
    private String NextInspection;
    private String Status;
    private List<ChecklistItem> ChecklistItems;

    /**
     * Constructs a new instance of Checklist
     */
    public Checklist() {
        ChecklistItems = new ArrayList<>();
    }

    /**
     * Getter method for the ID of the Checklist
     * @return The ID of the current Checklist
     */
    public String getChecklistID() {
        return ChecklistID;
    }

    /**
     * Sets the ID of the current Checklist
     * @param checklistID ID of the current Checklist
     */
    public void setChecklistID(String checklistID) {
        ChecklistID = checklistID;
    }

    /**
     * Getter method for the equipmentID assigned to the Checklist
     * @return the equipment ID assigned to this checklist
     */
    public String getEquipmentID() {
        return EquipmentID;
    }

    /**
     * Sets the equipment ID to be assigned to the Checklist
     * @param equipmentID The ID of the equipment
     */
    public void setEquipmentID(String equipmentID) {
        EquipmentID = equipmentID;
    }

    /**
     * Getter method for the name of the equipment assigned to the Checklist
     * @return The name of the equipment assigned to the Checklist
     */
    public String getEquipmentName() {
        return EquipmentName;
    }

    /**
     * Sets the name of the equipment assigned to the current Checklist
     * @param equipmentName
     */
    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    /**
     * Getter method for the last inspection date
     * @return The date of the last inspection
     */
    public String getLastInspection() {
        return LastInspection;
    }

    /**
     * Sets the date of the last inspection
     * @param lastInspection Date of the last inspection
     */
    public void setLastInspection(String lastInspection) {
        LastInspection = lastInspection;
    }

    /**
     * Getter method for the date of the next inspection
     * @return The date of the next inspection
     */
    public String getNextInspection() {
        return NextInspection;
    }

    /**
     * Sets the date of the next inspection
     * @param nextInspection The date of the next inspection
     */
    public void setNextInspection(String nextInspection) {
        NextInspection = nextInspection;
    }

    /**
     * Getter method for the current equipment status
     * @return The status of the equipment
     */
    public String getStatus() {
        return Status;
    }

    /**
     * Sets the status for the current equipment
     * @param status {@code "Good to Go"} or {@code "Do Not Use"}
     */
    public void setStatus(String status) {
        Status = status;
    }

    /**
     * Getter method for the items within the Checklist
     * @return List containing all items within the Checklist
     */
    public List<ChecklistItem> getChecklistItems() {
        return ChecklistItems;
    }

    /**
     * Sets the items to be assigned to the Checklist
     * @param checklistItems Items to be assigned to the Checklist
     */
    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        ChecklistItems = checklistItems;
    }

    /**
     * Adds on item to the Checklist
     * @param item item to be added to the Checklist
     */
    public void addChecklistItem(ChecklistItem item) {
        ChecklistItems.add(item);
    }
}
