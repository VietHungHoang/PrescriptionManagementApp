package com.mad.prescriptionmanagementapp.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

// import com.yourapp.network.BackendApiService; // Nếu gọi API trực tiếp
// import com.yourapp.worker.UpdateReminderStatusWorker; // Nếu dùng WorkManager

import com.mad.prescriptionmanagementapp.api.MedicineApi;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
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
                ScheduleEntityDTO reminder = db.scheduleDao().getById(reminderId);
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
                        sendStatusToBackend(Instant.ofEpochMilli(reminder.getScheduleEntity().getScheduledDateTimeMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime().toString(), 2, LocalDateTime.now().toString(), true);
                        break;

                    case Constants.ACTION_SNOOZE:
                        Log.d(TAG, "Processing SNOOZE for reminder ID: " + reminderId);
                        long snoozeUntilMillis = System.currentTimeMillis() + (long) Constants.SNOOZE_DURATION_MINUTES * 60 * 1000;
                        reminder.getScheduleEntity().setScheduledDateTimeMillis(snoozeUntilMillis);
                        reminder.getScheduleEntity().setStatus(ReminderStatus.PENDING); // Đặt lại PENDING để AlarmScheduler xử lý
                        // Hoặc bạn có thể có status SNOOZED riêng và AlarmScheduler cũng query status này
//                        db.scheduleDao().update(reminder); // Cập nhật thời gian và status

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
                        sendStatusToBackend(Instant.ofEpochMilli(reminder.getScheduleEntity().getScheduledDateTimeMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime().toString(), 1, LocalDateTime.now().toString(), true);
                        break;
                }
            }
        });
    }

    private void sendStatusToBackend(String defaultTimeString, int status, String selectedTimeString, boolean editted) {
        if (defaultTimeString == null || selectedTimeString == null) {
            Log.e("STATUS", "Thời gian không hợp lệ");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.11.78.222:8080/")  // Đảm bảo URL là chính xác (nếu dùng Emulator)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Khởi tạo api chỉ khi chưa khởi tạo
        MedicineApi medicineApi = retrofit.create(MedicineApi.class);

        // Tạo request với chuỗi thời gian
        StatusUpdateRequest request = new StatusUpdateRequest(defaultTimeString, status, selectedTimeString,editted);

        // Gửi yêu cầu đến backend
        medicineApi.updateStatus(request).enqueue(new Callback<StatusUpdateResponse>() {
            @Override
            public void onResponse(Call<StatusUpdateResponse> call, Response<StatusUpdateResponse> response) {
                if (response.isSuccessful()) {
                    StatusUpdateResponse statusUpdateResponse = response.body();
                    Log.d("STATUS", "Cập nhật thành công: " + statusUpdateResponse.getMessage());
                } else {
                    Log.e("STATUS", "Cập nhật thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatusUpdateResponse> call, Throwable t) {
                Log.e("STATUS", "Lỗi gửi trạng thái: " + t.getMessage(), t);
            }
        });
    }
}