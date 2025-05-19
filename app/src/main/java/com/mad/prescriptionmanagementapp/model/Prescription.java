package com.mad.prescriptionmanagementapp.model;

public class Prescription  extends  BaseItem{
    private String drugName;
    private String schedule;


    // Constructor
    public Prescription(String drugName, String schedule) {
        this.drugName = drugName;
        this.schedule = schedule;

    }

    // Getter and Setter
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }


    @Override
    public int getType() {
        return TYPE_PRESCRIPTION;
    }
}
