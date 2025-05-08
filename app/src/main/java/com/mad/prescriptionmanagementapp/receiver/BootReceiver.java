package com.mad.prescriptionmanagementapp.receiver;

// com.yourpackage.receiver.BootReceiver.java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mad.prescriptionmanagementapp.util.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "Device boot completed. Rescheduling alarms...");
            AlarmScheduler alarmScheduler = new AlarmScheduler(context.getApplicationContext());
            alarmScheduler.scheduleAllPendingReminders();
            Log.i(TAG, "Alarms rescheduled.");
        }
    }
}