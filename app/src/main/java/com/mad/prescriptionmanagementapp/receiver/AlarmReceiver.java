//package com.mad.prescriptionmanagementapp.receiver;
//
//import static com.mad.prescriptionmanagementapp.scheduler.AlarmSchedulerImpl.ACTION_REMINDER_ALARM;
//import static com.mad.prescriptionmanagementapp.scheduler.AlarmSchedulerImpl.EXTRA_SCHEDULE_ID;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
//
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "AlarmReceiver";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (context == null || intent == null || intent.getAction() == null) {
//            return;
//        }
//
//        // Chỉ xử lý action của chúng ta
//        if (ACTION_REMINDER_ALARM.equals(intent.getAction())) {
//            long scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L);
//            // long medicationId = intent.getLongExtra(AlarmSchedulerImpl.EXTRA_MEDICATION_ID, -1L); // Lấy nếu cần
//
//            if (scheduleId == -1L) {
//                Log.e(TAG, "Received alarm intent with invalid scheduleId.");
//                return;
//            }
//
//            Log.i(TAG, "Alarm received for scheduleId: " + scheduleId);
//
//            // *** KHÔNG xử lý lâu ở đây ***
//            // Khởi chạy Foreground Service để hiển thị thông báo
//            Intent serviceIntent = new Intent(context, ReminderNotificationService.class);
//            serviceIntent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
//            // serviceIntent.putExtra(AlarmSchedulerImpl.EXTRA_MEDICATION_ID, medicationId); // Truyền nếu cần
//
//            // Khởi chạy service
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent);
//            } else {
//                context.startService(serviceIntent);
//            }
//        }
//    }
//}