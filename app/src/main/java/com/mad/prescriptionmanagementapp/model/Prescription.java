package com.mad.prescriptionmanagementapp.model;

public class Prescription {
    private String title;
    private String medicines;

    public Prescription(String title, String medicines) {
        this.title = title;
        this.medicines = medicines;
    }

    public String getTitle() {
        return title;
    }

    public String getMedicines() {
        return medicines;
    }
}
