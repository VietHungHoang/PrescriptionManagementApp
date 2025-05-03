package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import com.mad.prescriptionmanagementapp.data.model.Doctor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrescriptionRequest {
    private List<DrugInPres> drugs = new ArrayList<>();
    private String hospital;
    private Doctor doctor;
    private String doctorName;
    private LocalDate consultationDate;
    private LocalDate followUpDate;

    public void addDrug(DrugInPres drug) {
        this.drugs.add(drug);
    }
}