package com.mad.prescriptionmanagementapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.mad.prescriptionmanagementapp.reminder.ReminderWorker;

import java.util.concurrent.TimeUnit;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("done".equals(action)) {
            // Xử lý khi người dùng chọn "Đã uống"
            Toast.makeText(context, "Đã uống thuốc", Toast.LENGTH_SHORT).show();
            // Ví dụ: dừng lại thông báo, hoặc update trạng thái
        } else if ("snooze".equals(action)) {
            // Xử lý khi người dùng chọn "Nhắc lại sau"
            Toast.makeText(context, "Nhắc lại sau 1 tiếng", Toast.LENGTH_SHORT).show();
            // Có thể lên lịch lại thông báo sau 1 tiếng nữa
            setSnoozeReminder(context);
        }
    }

    private void setSnoozeReminder(Context context) {
        // Lên lịch lại sau 1 tiếng
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                ReminderWorker.class,
                1, TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "snoozeReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );
    }
}

