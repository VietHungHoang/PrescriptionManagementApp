package com.mad.prescriptionmanagementapp.data.model;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugInPres {
    private DrugResponse drugResponse;
    private Unit unit;
    private String date;
    private List<TimeDosage> timeDosages = new ArrayList<>();
    private Frequency frequency = Frequency.DAILY;
    private int everyNDays; // nếu type = EVERY_N_DAYS
    private List<Integer> specificDays; // nếu type = SPECIFIC_DATES

    public DrugInPres(DrugResponse drugResponse) {
        this.drugResponse = drugResponse;
        this.timeDosages = new ArrayList<>();
    }

    public void addTimeDosage(TimeDosage timeDosage) {
       this.timeDosages.add(timeDosage);
    }

    public DrugInPres(DrugInPres original) {
        this.drugResponse = original.drugResponse;
        this.timeDosages = new ArrayList<>(original.timeDosages); // shallow copy, đủ xài nếu TimeDosage immutable
    }
}
