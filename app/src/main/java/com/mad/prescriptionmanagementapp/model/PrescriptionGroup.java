package com.mad.prescriptionmanagementapp.model;

import java.util.List;

public class PrescriptionGroup extends BaseItem {
    private String groupName;
    private List<Prescription> medicineList;

    public PrescriptionGroup(String groupName, List<Prescription> medicineList) {
        this.groupName = groupName;
        this.medicineList = medicineList;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Prescription> getMedicineList() {
        return medicineList;
    }

    @Override
    public int getType() {
        return TYPE_GROUP;
    }
}
