package com.mad.prescriptionmanagementapp.data.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Prescription {
    private String name;
    private List<DrugInPres> drugs = new ArrayList<>();
    private String hospital;
    private String doctorName;
    private String consultationDate;
    private String followUpDate;

    public void addDrug(DrugInPres drug) {
        this.drugs.add(drug);
    }

    public Prescription(String name, String hospital, String doctorName, String consultationDate, String followUpDate) {
        this.name = name;
        this.hospital = hospital;
        this.doctorName = doctorName;
        this.consultationDate = consultationDate;
        this.followUpDate = followUpDate;
    }

}