package com.mad.prescriptionmanagementapp.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.mad.prescriptionmanagementapp.broadcast.NotificationBroadcastReceiver;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.util.List;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";

    public static void scheduleAlarmsForPendingReminders(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
        // Chạy trên background thread
        new Thread(() -> {
            List<ScheduleEntity> pendingSchedules = db.scheduleDao()
                    .getPendingReminders(ReminderStatus.PENDING, System.currentTimeMillis());

            for (ScheduleEntity schedule : pendingSchedules) {
                scheduleAlarm(context, schedule);
            }
            Log.d(TAG, "Scheduled " + pendingSchedules.size() + " alarms.");
        }).start();
    }

    public static void scheduleAlarm(Context context, ScheduleEntity schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        // Đưa ID của ScheduledReminderEntity (hoặc alarmManagerRequestId) để Receiver biết xử lý reminder nào
        intent.putExtra(NotificationBroadcastReceiver.EXTRA_REMINDER_ID, schedule.getLocalId());
        intent.putExtra(NotificationBroadcastReceiver.EXTRA_ALARM_REQUEST_CODE, schedule.getAlarmManagerRequestId());
        // Quan trọng: Action khác nhau hoặc data khác nhau (uri) để tạo PendingIntent khác nhau
        // Ở đây ta dùng request code duy nhất là đủ
        intent.setAction("REMINDER_TRIGGER_" + schedule.getAlarmManagerRequestId()); // Action duy nhất

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                schedule.getAlarmManagerRequestId(), // Request code phải là duy nhất cho mỗi alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Kiểm tra quyền SCHEDULE_EXACT_ALARM cho Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Cannot schedule exact alarms. App needs SCHEDULE_EXACT_ALARM permission or user setting.");
                // TODO: Hướng dẫn người dùng bật quyền hoặc dùng setWindow()
                // For now, we might fall back or just log.
                // As a fallback, you could use a less exact alarm, but for medication, exact is preferred.
            }
        }

        try {
            // Sử dụng setExactAndAllowWhileIdle để cố gắng hoạt động cả trong Doze mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, schedule.getScheduledDateTimeMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, schedule.getScheduledDateTimeMillis(), pendingIntent);
            }
            Log.d(TAG, "Scheduled alarm for reminder ID: " + schedule.getLocalId() + " at " + schedule.getScheduledDateTimeMillis() + " with request code: " + schedule.getAlarmManagerRequestId());
        } catch (SecurityException se) {
            Log.e(TAG, "SecurityException: Missing SCHEDULE_EXACT_ALARM or USE_EXACT_ALARM permission.", se);
            // Xử lý trường hợp thiếu quyền
        }
    }

    public static void cancelAlarm(Context context, int alarmManagerRequestId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        // Action phải giống như khi tạo
        intent.setAction("REMINDER_TRIGGER_" + alarmManagerRequestId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmManagerRequestId,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE để không tạo mới nếu chưa có
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel(); // Hủy cả PendingIntent
            Log.d(TAG, "Canceled alarm with request code: " + alarmManagerRequestId);
        } else {
            Log.d(TAG, "PendingIntent not found for request code: " + alarmManagerRequestId + ", cannot cancel.");
        }
    }
}