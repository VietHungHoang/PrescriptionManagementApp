package com.mad.prescriptionmanagementapp.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;

// import com.yourapp.network.BackendApiService; // Nếu gọi API trực tiếp
// import com.yourapp.worker.UpdateReminderStatusWorker; // Nếu dùng WorkManager

import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.remote.api.kiet.MedicineApi;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entitydto.ScheduleEntityDTO;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.kiet.StatusUpdateRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.StatusUpdateResponse;
import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;
import com.mad.prescriptionmanagementapp.util.Constants;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActionHandlerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionHandlerReceiver";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }

        int notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, -1);
        ArrayList<Long> reminderIds = (ArrayList<Long>) intent.getSerializableExtra(Constants.EXTRA_REMINDER_IDS_LIST);

        if (reminderIds == null || reminderIds.isEmpty()) {
            Log.e(TAG, "Reminder IDs list is null or empty for action: " + action);
            return;
        }

        Log.d(TAG, "Action: " + action + ", Notification ID: " + notificationId + ", Reminder IDs: " + reminderIds.toString());

        // Hủy thông báo (Xoá khỏi thanh thông báo)
        if (notificationId != -1) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);
            Log.d(TAG, "Cancelled notification ID: " + notificationId);
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
            long reminderId = reminderIds.get(0);
            ScheduleEntityDTO scheduleEntityDTO = db.scheduleDao().getById(reminderId);
                List<ScheduleEntityDTO> reminders = db.scheduleDao().getByAlarmManagerRequestId(scheduleEntityDTO.getScheduleEntity().getAlarmManagerRequestId());
                if (reminders == null) {
                    Log.w(TAG, "Reminder not found in DB for ID: " + reminderId + " during action: " + action);
                }

                switch (action) {
                    case Constants.ACTION_CONFIRM:
                        db.scheduleDao().updateStatus(reminderId, ReminderStatus.CONFIRMED);
                        // TODO: Gửi lên backend (dùng WorkManager là tốt nhất)
                        // BackendApiService.getInstance().confirmReminder(reminderId);
//                         WorkManager.enqueue(UpdateReminderStatusWorker.forConfirm(reminderId));
                        Log.i(TAG, "Reminder ID " + reminderId + " Confirmed.");
//
                        break;

                    case Constants.ACTION_SNOOZE:
                        Log.d(TAG, "Processing SNOOZE for reminder ID: " + reminderId);
                        AlarmScheduler.scheduleAlarmByTime(context, scheduleEntityDTO.getScheduleEntity(), 15);
                        break;

                    case Constants.ACTION_SKIP:
                        Log.d(TAG, "Processing SKIP for reminder ID: " + reminderId);
                        db.scheduleDao().updateStatus(reminderId, ReminderStatus.SKIPPED);
                        // TODO: Gửi lên backend (dùng WorkManager)
                        // BackendApiService.getInstance().skipReminder(reminderId);
                        Log.i(TAG, "Reminder ID " + reminderId + " Skipped.");
//                        sendStatusToBackend(Instant.ofEpochMilli(reminder.getScheduleEntity().getScheduledDateTimeMillis())
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDateTime().toString(), 1, LocalDateTime.now().toString(), true);
                        break;
            }
        });
    }
}