package com.slebbers.dunl08.database;

import android.provider.BaseColumns;


public final class ChecklistDataContract {

    private ChecklistDataContract() {}

    public static class ChecklistEntry implements BaseColumns {
        public static final String TABLE_NAME = "Checklist";
        public static final String COLUMN_CHECKLIST_ID_PK = "ChecklistID";
        public static final String COLUMN_CHECKLIST_TYPE = "ChecklistType";
    }

    public static class ChecklistItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "ChecklistItem";
        public static final String COLUMN_CHECKLISTITEM_ID_PK = "ChecklistItemID";
        public static final String COLUMN_CHECKLISTITEM = "ChecklistItem";
        public static final String COLUMN_CHECKLIST_ID_FK = "ChecklistID";
    }

    public static class EquipmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "Equipment";
        public static final String COLUMN_EQUIPMENT_ID_PK = "EquipmentID";
        public static final String COLUMN_EQUIPMENT_NAME = "EquipmentName";
        public static final String COLUMN_LAST_INSPECTION = "LastInspection";
        public static final String COLUMN_NEXT_INSPECTION = "NextInspection";
        public static final String COLUMN_PASS_FAIL = "Status";
        public static final String COLUMN_CHECKLIST_ID_FK = "ChecklistID";
    }

    public static class EquipmentItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "EquipmentItem";
        public static final String COLUMN_EQUIPMENTITEM_ID_PK = "EquipmentItemID";
        public static final String COLUMN_EQUIPMENT_ID_FK = "EquipmentID";
        public static final String COLUMN_CHECKLISTITEM_ID_FK = "ChecklistItemID";
        public static final String COLUMN_ISCHECKED = "IsChecked";
    }

}
