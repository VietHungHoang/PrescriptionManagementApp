package com.mad.prescriptionmanagementapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;

import java.util.Objects;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "Device boot completed. Re-scheduling alarms...");
            // Gọi hàm lập lịch lại tất cả các PENDING reminders
            AlarmScheduler.scheduleAlarmsForPendingReminders(context.getApplicationContext());
        }
    }
}