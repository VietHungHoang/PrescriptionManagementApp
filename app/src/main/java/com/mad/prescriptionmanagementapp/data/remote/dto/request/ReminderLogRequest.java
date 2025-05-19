package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLogRequest {

    private long scheduleId;

    private Long medicationId; // Use Long object type to allow null

    private String scheduledTimestamp; // ISO 8601 UTC string

    private String actionTimestamp; // ISO 8601 UTC string

    private String status; // "TAKEN" or "SKIPPED"

}