package com.mad.prescriptionmanagementapp.broadcast;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entitydto.ScheduleEntityDTO;
import com.mad.prescriptionmanagementapp.ui.activity.hung.HomeActivity;
import com.mad.prescriptionmanagementapp.util.Constants;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    public static final String EXTRA_REMINDER_ID = "extra_reminder_id";
    public static final String EXTRA_ALARM_REQUEST_CODE = "extra_alarm_request_code"; // Dùng để log hoặc debug
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        long reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1);
        int alarmRequestCode = intent.getIntExtra(EXTRA_ALARM_REQUEST_CODE, -1); // Lấy request code

        if (reminderId == -1) {
            return;
        }

        // Thực hiện các tác vụ DB và network trên background thread
        this.executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
            ScheduleEntityDTO mainReminder = db.scheduleDao().getById(reminderId);

            if (mainReminder == null || !ReminderStatus.PENDING.equals(mainReminder.getScheduleEntity().getStatus())) {
                // Có thể đã được xử lý (confirm, skip, snooze) hoặc xóa
                return;
            }

            List<ScheduleEntityDTO> remindersForThisTime = db.scheduleDao()
                    .getSnozeeByRequestIdAndStatus(mainReminder.getScheduleEntity().getAlarmManagerRequestId(), ReminderStatus.PENDING, ReminderStatus.NOTIFIED);

            if (remindersForThisTime.isEmpty()) {
                return;
            }

            int notificationId = mainReminder.getScheduleEntity().getAlarmManagerRequestId();
            // Tạo channel (chỉ cần làm 1 lần)
            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_REMINDERS);

            Intent tapIntent = new Intent(context, HomeActivity.class);
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Bạn có thể đính kèm thông tin để Activity biết cần hiển thị gì
            // tapIntent.putExtra("deep_link_target", "reminder_details");
            // tapIntent.putExtra("reminder_id", reminderId);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(
                    context,
                    notificationId,
                    tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            builder.setSmallIcon(R.drawable.logo)
                    .setContentTitle("Đến giờ uống thuốc!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(contentPendingIntent)
                    .setGroup("cuatao");
//                    .setGroupSummary(true);

            ArrayList<Long> reminderIdsInNotification = new ArrayList<>();
            for(ScheduleEntityDTO r : remindersForThisTime){
                reminderIdsInNotification.add(r.getScheduleEntity().getLocalId());
            }

            // Nội dung thông báo
            if (remindersForThisTime.size() == 1) {
                ScheduleEntityDTO singleReminder = remindersForThisTime.get(0);
                builder.setContentText(String.format(Locale.getDefault(), "Đơn thuốc: %s => %s %s %s",
                        singleReminder.getDrugInPresDTO().getPrescriptionEntity().getName(), singleReminder.getDrugInPresDTO().getDrugEntity().getName(), Tools.formatNumber(singleReminder.getDosage().getDosage()), singleReminder.getDrugInPresDTO().getUnitEntity().getName()));
            } else {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("Đã đến giờ uống thuốc!");
                Map<String, List<ScheduleEntityDTO>> grouped = new LinkedHashMap<>();

// Nhóm theo tên đơn thuốc
                for (ScheduleEntityDTO r : remindersForThisTime) {
                    String presName = r.getDrugInPresDTO().getPrescriptionEntity().getName();
                    grouped.putIfAbsent(presName, new ArrayList<>());
                    grouped.get(presName).add(r);
                }

                StringBuilder summaryText = new StringBuilder();

                for (Map.Entry<String, List<ScheduleEntityDTO>> entry : grouped.entrySet()) {
                    String presName = entry.getKey();
                    List<ScheduleEntityDTO> list = entry.getValue();

                    inboxStyle.addLine("[" + presName + "]");
                    for (ScheduleEntityDTO r : list) {
                        String line = String.format(Locale.getDefault(), "- Uống: %s %s %s",
                                r.getDrugInPresDTO().getDrugEntity().getName(),
                                Tools.formatNumber(r.getDosage().getDosage()),
                                r.getDrugInPresDTO().getUnitEntity().getName());
                        inboxStyle.addLine(line);
                    }

                    // Tạo summaryText gọn gọn
                    if (summaryText.length() == 0 && !list.isEmpty()) {
                        ScheduleEntityDTO first = list.get(0);
                        summaryText.append(String.format(Locale.getDefault(), "Uống: %s %s %s",
                                first.getDrugInPresDTO().getDrugEntity().getName(),
                                Tools.formatNumber(first.getDosage().getDosage()),
                                first.getDrugInPresDTO().getUnitEntity().getName()));
                        if (remindersForThisTime.size() > 1) {
                            summaryText.append("... và các thuốc khác");
                        }
                    }
                }

                builder.setContentText(summaryText.toString());
                builder.setStyle(inboxStyle);

            }


            // Action: Confirm
            Intent confirmIntent = new Intent(context, ActionHandlerBroadcastReceiver.class);
            confirmIntent.setAction(Constants.ACTION_CONFIRM);
            confirmIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
            confirmIntent.putExtra(Constants.EXTRA_REMINDER_IDS_LIST, reminderIdsInNotification);
            PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 1, // Unique request code
                    confirmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            builder.addAction(R.drawable.check_selector, "Đã uống", confirmPendingIntent);

            // Action: Snooze
            Intent snoozeIntent = new Intent(context, ActionHandlerBroadcastReceiver.class);
            snoozeIntent.setAction(Constants.ACTION_SNOOZE);
            snoozeIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
            snoozeIntent.putExtra(Constants.EXTRA_REMINDER_IDS_LIST, reminderIdsInNotification);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 2, // Unique request code
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            builder.addAction(R.drawable.ic_action_snooze, "Nhắc sau 15p", snoozePendingIntent);

            // Action: Skip
            Intent skipIntent = new Intent(context, ActionHandlerBroadcastReceiver.class);
            skipIntent.setAction(Constants.ACTION_SKIP);
            skipIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
            skipIntent.putExtra(Constants.EXTRA_REMINDER_IDS_LIST, reminderIdsInNotification);
            PendingIntent skipPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 3, // Unique request code
                    skipIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            builder.addAction(R.drawable.ic_action_skip, "Bỏ qua", skipPendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // Xin quyền POST_NOTIFICATIONS nếu target API 33+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (notificationManager.areNotificationsEnabled()) { // Cần check permission thực tế
                    Log.d(TAG, "Sending notification ID: " + notificationId);
                    notificationManager.notify(notificationId, builder.build());
                } else {
                    Log.w(TAG, "Notification permission not granted.");
                    // Cần có cơ chế yêu cầu quyền từ người dùng
                }
            } else {
                Log.d(TAG, "Sending notification ID: " + notificationId);
                notificationManager.notify(notificationId, builder.build());
            }


            // Cập nhật trạng thái các reminder đã được thông báo
            for (ScheduleEntityDTO r : remindersForThisTime) {
                db.scheduleDao().updateStatus(r.getScheduleEntity().getLocalId(), ReminderStatus.NOTIFIED);
            }
        });
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = context.getString(R.string.notification_channel_reminders_name);
//            String description = context.getString(R.string.notification_channel_reminders_description);
            CharSequence name = "hello ku";
            String description = "gi vay lu";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Quan trọng cao cho nhắc nhở
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_REMINDERS, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            // Bạn có thể set âm thanh riêng channel.setSound(soundUri, audioAttributes);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created/updated.");
            }
        }
    }

    // Hàm helper để tải ảnh từ URL (chạy trên background thread)
//    public static Bitmap getBitmapFromURL(String src) {
//        if (src == null || src.isEmpty()) return null;
//        try {
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            return BitmapFactory.decodeStream(input);
//        } catch (IOException e) {
//            Log.e(TAG, "Error downloading image: " + src, e);
//            return null;
//        }
//    }
}