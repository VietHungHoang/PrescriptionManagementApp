package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import com.mad.prescriptionmanagementapp.data.model.Doctor;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrescriptionRequest {
    private String name;
    private List<DrugInPres> drugs = new ArrayList<>();
    private String hospital;
    private Doctor doctor;
    private String doctorName;
    private String consultationDate;
    private String followUpDate;

    public void addDrug(DrugInPres drug) {
        this.drugs.add(drug);
    }

    public PrescriptionRequest(String name, String hospital, String doctorName, String consultationDate, String followUpDate) {
        this.name = name;
        this.hospital = hospital;
        this.doctorName = doctorName;
        this.consultationDate = consultationDate;
        this.followUpDate = followUpDate;
    }

}