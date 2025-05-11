package com.mad.prescriptionmanagementapp.broadcast;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entitydto.ScheduleEntityDTO;
import com.mad.prescriptionmanagementapp.ui.activity.HomeActivity;
import com.mad.prescriptionmanagementapp.util.Constants;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        Log.d(TAG, "onReceive: Received alarm trigger");
        long reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1);
        int alarmRequestCode = intent.getIntExtra(EXTRA_ALARM_REQUEST_CODE, -1); // Lấy request code
        Log.d(TAG, "Received reminder ID: " + reminderId + ", AlarmRequestCode: " + alarmRequestCode);

        if (reminderId == -1) {
            Log.e(TAG, "Invalid reminder ID received.");
            return;
        }

        // Thực hiện các tác vụ DB và network trên background thread
        this.executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
            ScheduleEntityDTO mainReminder = db.scheduleDao().getById(reminderId);

            if (mainReminder == null || !ReminderStatus.PENDING.equals(mainReminder.getScheduleEntity().getStatus())) {
                Log.w(TAG, "Reminder not found or not in PENDING state for ID: " + reminderId);
                // Có thể đã được xử lý (confirm, skip, snooze) hoặc xóa
                return;
            }

            // Nhóm các thuốc có cùng thời gian (ví dụ trong 1 phút)
            long timeWindowStart = mainReminder.getScheduleEntity().getScheduledDateTimeMillis() - (30 * 1000); // 30 giây trước
            long timeWindowEnd = mainReminder.getScheduleEntity().getScheduledDateTimeMillis() + (30 * 1000);   // 30 giây sau

            List<ScheduleEntityDTO> remindersForThisTime = db.scheduleDao()
                    .getRemindersAroundTime(timeWindowStart, timeWindowEnd, ReminderStatus.PENDING);

            if (remindersForThisTime.isEmpty()) {
                Log.w(TAG, "No PENDING reminders found around this time for main reminder ID: " + reminderId);
                return; // Không còn reminder PENDING nào tại thời điểm này
            }

            // Tạo Notification ID (có thể dùng alarmRequestCode của reminder chính)
            // hoặc một ID mới nếu bạn nhóm nhiều reminder vào 1 notif
            int notificationId = mainReminder.getScheduleEntity().getAlarmManagerRequestId(); // Dùng request code làm notification ID

            // Tạo channel (chỉ cần làm 1 lần)
            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_REMINDERS);

            // Intent khi nhấn vào thông báo (mở app)
            Intent tapIntent = new Intent(context, HomeActivity.class); // Thay bằng Activity bạn muốn mở
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Bạn có thể đính kèm thông tin để Activity biết cần hiển thị gì
            // tapIntent.putExtra("deep_link_target", "reminder_details");
            // tapIntent.putExtra("reminder_id", reminderId);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(
                    context,
                    notificationId, // request code cho content intent
                    tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            builder.setSmallIcon(R.drawable.ic_notification_icon) // Thay bằng icon của bạn
                    .setContentTitle("Đến giờ uống thuốc!")
                    .setPriority(NotificationCompat.PRIORITY_MAX) // Ưu tiên cao cho nhắc nhở quan trọng
                    .setDefaults(NotificationCompat.DEFAULT_ALL) // Âm thanh, rung, đèn LED mặc định
                    .setAutoCancel(false) // Không tự hủy khi chạm, chỉ hủy khi có action
                    .setOngoing(true) // Làm cho thông báo không thể vuốt đi (cân nhắc UX)
                    .setContentIntent(contentPendingIntent);


            ArrayList<Long> reminderIdsInNotification = new ArrayList<>();
            for(ScheduleEntityDTO r : remindersForThisTime){
                reminderIdsInNotification.add(r.getScheduleEntity().getLocalId());
            }

            // Nội dung thông báo
            if (remindersForThisTime.size() == 1) {
                ScheduleEntityDTO singleReminder = remindersForThisTime.get(0);
                builder.setContentText(String.format(Locale.getDefault(), "Uống: %s %s %s",
                        singleReminder.getScheduleEntity().getLocalId(), singleReminder.getScheduleEntity().getLocalId(), singleReminder.getScheduleEntity().getLocalId()));
//                if (!TextUtils.isEmpty(singleReminder.drugImage)) {
//                    Bitmap largeIcon = getBitmapFromURL(singleReminder.drugImage);
//                    if (largeIcon != null) {
//                        builder.setLargeIcon(largeIcon);
//                    }
//                }
            } else {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("Đến giờ uống thuốc!");
                StringBuilder summaryText = new StringBuilder();
                for (int i = 0; i < remindersForThisTime.size(); i++) {
                    ScheduleEntityDTO r = remindersForThisTime.get(i);
                    String line = String.format(Locale.getDefault(), "• %s %s %s",
                            r.getScheduleEntity().getLocalId(), r.getScheduleEntity().getLocalId(), r.getScheduleEntity().getLocalId());
                    inboxStyle.addLine(line);
                    if (i < 2) { // Hiển thị 2 dòng đầu ở dạng thu gọn
                        summaryText.append(line).append(i == 0 && remindersForThisTime.size() > 1 ? " | " : "");
                    }
                }
                if (remindersForThisTime.size() > 2) {
                    summaryText.append("... và các thuốc khác");
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