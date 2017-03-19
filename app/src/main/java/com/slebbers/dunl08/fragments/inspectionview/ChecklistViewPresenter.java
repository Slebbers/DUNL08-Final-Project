package com.slebbers.dunl08.fragments.inspectionview;

public interface ChecklistViewPresenter {
    void onStart();
    void setEquipmentID(String equipmentID);
    void btnReinspectClick();
    void btnSubmitClick();
}
