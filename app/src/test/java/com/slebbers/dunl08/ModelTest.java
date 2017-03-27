package com.slebbers.dunl08;

import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.model.ChecklistItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


public class ModelTest {

    @Test
    public void checklist_instantiation() {
        assertNotNull(new Checklist());
    }

    @Test
    public void checklist_assignID() {
        Checklist checklist = new Checklist();
        String checklistID = "40";
        checklist.setChecklistID(checklistID);
        assertEquals(checklistID, checklist.getChecklistID());
    }

    @Test
    public void checklist_assignEquipmentID() {
        Checklist checklist = new Checklist();
        String equipmentID = "10";
        checklist.setEquipmentID(equipmentID);
        assertEquals(equipmentID, checklist.getEquipmentID());
    }

    @Test
    public void checklist_assignEquipmentName() {
        Checklist checklist = new Checklist();
        String equipmentName = "Big Truck";
        checklist.setEquipmentName(equipmentName);
        assertEquals(equipmentName, checklist.getEquipmentName());
    }

    @Test
    public void checklist_assignLastInspection() {
        Checklist checklist = new Checklist();
        String lastInspection = "01 Jan 2017";
        checklist.setLastInspection(lastInspection);
        assertEquals(lastInspection, checklist.getLastInspection());
    }

    @Test
    public void checklist_assignNextInspection() {
        Checklist checklist = new Checklist();
        String nextInspection = "01 Feb 2017";
        checklist.setLastInspection(nextInspection);
        assertEquals(nextInspection, checklist.getLastInspection());
    }

    @Test
    public void checklist_assignStatus() {
        Checklist checklist = new Checklist();
        String status = "Good to Go";
        checklist.setStatus(status);
        assertEquals(status, checklist.getStatus());
    }

    @Test
    public void checklist_assignChecklistItem() {
        Checklist checklist = new Checklist();
        ChecklistItem item = new ChecklistItem();
        checklist.addChecklistItem(item);
        assertTrue(checklist.getChecklistItems().contains(item));
    }

    @Test
    public void checklist_assignChecklistItems() {
        Checklist checklist = new Checklist();
        List<ChecklistItem> items = new ArrayList<>();
        ChecklistItem item1 = new ChecklistItem();
        ChecklistItem item2 = new ChecklistItem();
        items.add(item1);
        items.add(item2);
        checklist.setChecklistItems(items);

        assertTrue(checklist.getChecklistItems().contains(item1));
        assertTrue(checklist.getChecklistItems().contains(item2));
    }

    @Test
    public void checklistItem_assignItemID() {
        ChecklistItem item = new ChecklistItem();
        String itemID = "2";
        item.setChecklistItemID(itemID);
        assertEquals(itemID, item.getChecklistItemID());
    }

    @Test
    public void checklistItem_assignItem() {
        ChecklistItem item = new ChecklistItem();
        String checklistItem = "Check guard rail";
        item.setChecklistItem(checklistItem);
        assertEquals(checklistItem, item.getChecklistItem());
    }

    @Test
    public void checklistItem_assignIsChecked() {
        ChecklistItem item = new ChecklistItem();
        String checkedStatus = "1";
        item.setIsChecked(checkedStatus);
        assertEquals(checkedStatus, item.getIsChecked());
    }

    @Test
    public void checklistItem_assignIsEnabled() {
        ChecklistItem item = new ChecklistItem();
        item.setIsEnabled(true);
        assertTrue(item.getIsEnabled());
        item.setIsEnabled(false);
        assertFalse(item.getIsEnabled());
    }
}