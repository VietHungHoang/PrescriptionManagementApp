package com.mad.prescriptionmanagementapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lớp này nên được lấy từ DB, đây chỉ là cấu trúc ví dụ
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationReminderDetails {
    private long scheduleId;
    private Long medicationId; // Có thể null
    private String medicationName;
    private String dosage;
    private String instructions; // Có thể null
    private long scheduledTimeMillis; // Thời gian gốc UTC
}