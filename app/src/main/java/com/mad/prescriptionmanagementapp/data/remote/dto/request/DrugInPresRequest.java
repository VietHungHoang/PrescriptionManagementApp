package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugInPresRequest {
    private Long id;
    private List<TimeDosageRequest> timeDosages = new ArrayList<>();
    private List<ScheduleRequest> schedules = new ArrayList<>();

    public DrugInPresRequest (Long id) {
        this.id = id;
        this.schedules = new ArrayList<>();
    }

    public void addTimeDosage(TimeDosageRequest timeDosageRequest) {
       this.timeDosages.add(timeDosageRequest);
    }
}
