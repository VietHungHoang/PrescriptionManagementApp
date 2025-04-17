package com.mad.prescriptionmanagementapp.scheduler;

public interface AlarmScheduler {
    void scheduleReminder(long scheduleId, Long medicationId, long triggerTimeMillis);
    void scheduleSnooze(long scheduleId, Long medicationId); // Mặc định snooze 10 phút
    void scheduleSnooze(long scheduleId, Long medicationId, int snoozeMinutes); // Cho phép tùy chỉnh thời gian snooze
    void cancelAlarm(long scheduleId);
    void rescheduleAllActiveAlarms(); // Dùng cho boot complete
}