package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;

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
    private List<TimeDosageRequest> timeDosages = new ArrayList<>();
    private List<ScheduleRequest> schedules = new ArrayList<>();

    public DrugInPres(DrugResponse drugResponse) {
        this.drugResponse = drugResponse;
        this.schedules = new ArrayList<>();
    }

    public void addTimeDosage(TimeDosageRequest timeDosageRequest) {
       this.timeDosages.add(timeDosageRequest);
    }
}
