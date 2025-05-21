package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ScheduleAddRequest {
    private String date;
    private Double dosage;
}
