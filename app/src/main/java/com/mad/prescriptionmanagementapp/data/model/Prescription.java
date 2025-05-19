package com.mad.prescriptionmanagementapp.data.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Prescription {
    private Long id;
    private List<DrugInPres> drugs;
    private String hospital;
    private Doctor doctor;
    private String doctorName;
    private LocalDate consultationDate;
    private LocalDate followUpDate;
}
