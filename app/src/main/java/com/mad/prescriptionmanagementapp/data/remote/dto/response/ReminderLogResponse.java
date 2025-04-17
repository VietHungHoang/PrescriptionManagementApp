package com.mad.prescriptionmanagementapp.data.remote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLogResponse {

    private long logId;

    private long userId;

    private long scheduleId;

    private Long medicationId;

    private String scheduledTimestamp;

    private String actionTimestamp;

    private String status;

}
