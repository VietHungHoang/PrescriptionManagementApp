//package com.mad.prescriptionmanagementapp.receiver;
//
//package com.yourcompany.yourapp.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.yourcompany.yourapp.domain.scheduler.AlarmScheduler; // Interface scheduler
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint // Quan trọng: Cho phép inject vào Receiver
//public class BootCompletedReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "BootCompletedReceiver";
//
//    // Inject AlarmScheduler
//    @Inject
//    AlarmScheduler alarmScheduler;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent == null || intent.getAction() == null) return;
//
//        String action = intent.getAction();
//        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || "android.intent.action.QUICKBOOT_POWERON".equals(action)) {
//            Log.i(TAG, "Device booted or rebooted (" + action + "). Rescheduling alarms.");
//            if (alarmScheduler != null) {
//                // Gọi hàm đặt lại lịch trong AlarmScheduler đã inject
//                alarmScheduler.rescheduleAllActiveAlarms();
//            } else {
//                // Điều này không nên xảy ra với Hilt, nhưng là phòng ngừa
//                Log.e(TAG, "AlarmScheduler was null. Cannot reschedule alarms.");
//                // Có thể thử khởi tạo thủ công nếu Hilt không hoạt động đúng cách
//                // Hoặc sử dụng goAsync() và xử lý inject trong background
//            }
//        }
//    }
//}