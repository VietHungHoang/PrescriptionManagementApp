package com.mad.prescriptionmanagementapp.data.model;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
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
    private SimpleDrug simpleDrug;
    private Unit unit;
    private String startDate;
    private List<TimeDosage> timeDosages = new ArrayList<>();
    private Frequency frequency = Frequency.DAILY;
    private int everyNDays; // nếu type = EVERY_N_DAYS
    private List<Integer> specificDays; // nếu type = SPECIFIC_DATES
    private String note;

    public DrugInPres(SimpleDrug simpleDrug) {
        this.simpleDrug = simpleDrug;
        this.timeDosages = new ArrayList<>();
    }

    public void addTimeDosage(TimeDosage timeDosage) {
       this.timeDosages.add(timeDosage);
    }

    public DrugInPres(DrugInPres original) {
        this.simpleDrug = original.simpleDrug;
        this.timeDosages = new ArrayList<>(original.timeDosages); // shallow copy, đủ xài nếu TimeDosage immutable
    }

    public Drug getDrug() {
        Drug res = new Drug();
        res.setId(this.simpleDrug.getId());
        res.setName(this.simpleDrug.getName());
        return res;
    }
}
