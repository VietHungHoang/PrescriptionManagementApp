package com.mad.prescriptionmanagementapp.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

// import com.yourapp.network.BackendApiService; // Nếu gọi API trực tiếp
// import com.yourapp.worker.UpdateReminderStatusWorker; // Nếu dùng WorkManager

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;
import com.mad.prescriptionmanagementapp.util.Constants;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActionHandlerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionHandlerReceiver";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.e(TAG, "Action is null");
            return;
        }

        int notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, -1);
        ArrayList<Long> reminderIds = (ArrayList<Long>) intent.getSerializableExtra(Constants.EXTRA_REMINDER_IDS_LIST);

        if (reminderIds == null || reminderIds.isEmpty()) {
            Log.e(TAG, "Reminder IDs list is null or empty for action: " + action);
            // Nếu không có list IDs, có thể thử lấy ID đơn lẻ từ một extra khác nếu thiết kế vậy
            return;
        }

        Log.d(TAG, "Action: " + action + ", Notification ID: " + notificationId + ", Reminder IDs: " + reminderIds.toString());

        // Hủy thông báo
        if (notificationId != -1) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);
            Log.d(TAG, "Cancelled notification ID: " + notificationId);
        }


        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());

            for (long reminderId : reminderIds) {
                ScheduleEntity reminder = db.scheduleDao().getById(reminderId);
                if (reminder == null) {
                    Log.w(TAG, "Reminder not found in DB for ID: " + reminderId + " during action: " + action);
                    continue;
                }

                switch (action) {
                    case Constants.ACTION_CONFIRM:
                        Log.d(TAG, "Processing CONFIRM for reminder ID: " + reminderId);
                        db.scheduleDao().updateStatus(reminderId, ReminderStatus.CONFIRMED);
                        // TODO: Gửi lên backend (dùng WorkManager là tốt nhất)
                        // BackendApiService.getInstance().confirmReminder(reminderId);
                        // Hoặc WorkManager.enqueue(UpdateReminderStatusWorker.forConfirm(reminderId));
                        Log.i(TAG, "Reminder ID " + reminderId + " Confirmed.");
                        break;

                    case Constants.ACTION_SNOOZE:
                        Log.d(TAG, "Processing SNOOZE for reminder ID: " + reminderId);
                        long snoozeUntilMillis = System.currentTimeMillis() + (long) Constants.SNOOZE_DURATION_MINUTES * 60 * 1000;
                        reminder.scheduledDateTimeMillis = snoozeUntilMillis;
                        reminder.status = ReminderStatus.PENDING; // Đặt lại PENDING để AlarmScheduler xử lý
                        // Hoặc bạn có thể có status SNOOZED riêng và AlarmScheduler cũng query status này
                        db.scheduleDao().update(reminder); // Cập nhật thời gian và status

                        // Đặt lại alarm
                        AlarmScheduler.scheduleAlarm(context, reminder);
                        // TODO: (Tùy chọn) Gửi thông tin snooze lên backend
                        Log.i(TAG, "Reminder ID " + reminderId + " Snoozed until " + snoozeUntilMillis);
                        break;

                    case Constants.ACTION_SKIP:
                        Log.d(TAG, "Processing SKIP for reminder ID: " + reminderId);
                        db.scheduleDao().updateStatus(reminderId, ReminderStatus.SKIPPED);
                        // TODO: Gửi lên backend (dùng WorkManager)
                        // BackendApiService.getInstance().skipReminder(reminderId);
                        Log.i(TAG, "Reminder ID " + reminderId + " Skipped.");
                        break;
                }
            }
        });
    }
}