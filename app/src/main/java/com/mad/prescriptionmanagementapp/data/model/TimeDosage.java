package com.mad.prescriptionmanagementapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeDosage {
    private int hour;
    private int minutes;
    private double dosage;
}
