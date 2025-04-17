//package com.mad.prescriptionmanagementapp.service;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioAttributes;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//
//import java.util.concurrent.Executor; // Executor cho tác vụ nền
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint // Cho phép inject vào Service
//public class ReminderNotificationService extends Service {
//
//    private static final String TAG = "ReminderNotificationSvc";
//    public static final String CHANNEL_ID = "MEDICATION_REMINDER_CHANNEL";
//    public static final String CHANNEL_NAME = "Nhắc nhở uống thuốc";
//    private static final int NOTIFICATION_ID_BASE = 2000;
//
//    @Inject ReminderRepository reminderRepository;
//    NotificationManager notificationManager;
//    @Inject Executor ioExecutor; // Inject Executor để chạy lấy dữ liệu
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "Service onCreate");
//        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        this.createNotificationChannel();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "Service onStartCommand");
//        if (intent == null) {
//            Log.w(TAG, "Intent is null, stopping service.");
//            this.stopSelf(startId);
//            return Service.START_NOT_STICKY;
//        }
//
//        long scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L);
//
//        if (scheduleId == -1L) {
//            Log.e(TAG, "Invalid scheduleId received. Stopping service.");
//            stopSelf(startId);
//            return START_NOT_STICKY;
//        }
//
//        Log.i(TAG, "Service started for scheduleId: " + scheduleId);
//
//        // Chạy lấy dữ liệu và hiển thị thông báo trên background thread
//        ioExecutor.execute(() -> {
//            try {
//                // Lấy chi tiết từ repository (sử dụng hàm blocking)
//                MedicationReminderDetails details = reminderRepository.getReminderDetailsBlocking(scheduleId);
//
//                if (details == null) {
//                    Log.e(TAG, "Could not get reminder details for schedule " + scheduleId + ". Stopping.");
//                    stopSelf(startId); // Dừng service nếu không có dữ liệu
//                    return;
//                }
//
//                // Tạo và hiển thị thông báo
//                Notification notification = createReminderNotification(details);
//
//                // Đưa service lên foreground
//                // ID phải là duy nhất cho mỗi lần nhắc đang hoạt động
//                int notificationId = NOTIFICATION_ID_BASE + (int) scheduleId;
//                startForeground(notificationId, notification);
//
//                Log.i(TAG, "Notification shown and service in foreground for schedule " + scheduleId);
//
//                // Service sẽ chạy cho đến khi bị dừng (ví dụ: bởi ReminderActivity)
//
//            } catch (Exception e) {
//                Log.e(TAG, "Error processing reminder for schedule " + scheduleId, e);
//                stopSelf(startId); // Dừng service nếu có lỗi
//            }
//        });
//
//        // START_REDELIVER_INTENT: Khởi động lại service với intent cuối cùng nếu bị kill
//        return START_REDELIVER_INTENT;
//    }
//
//    private Notification createReminderNotification(MedicationReminderDetails details) {
//        // Intent để mở ReminderActivity
//        Intent fullScreenIntent = new Intent(this,  ReminderActivity.class);
//        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        fullScreenIntent.putExtra(ReminderActivity.EXTRA_SCHEDULE_ID, details.getScheduleId());
//
//        // PendingIntent cho Full Screen và Content Intent
//        int uniqueRequestCode = (int) details.getScheduleId(); // Dùng ID làm request code
//        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
//                this,
//                uniqueRequestCode,
//                fullScreenIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Flag phù hợp
//        );
//
//        // Lấy âm báo mặc định
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmSound == null) {
//            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//
//        // Xây dựng Notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_medication_notification) // Icon của bạn
//                .setContentTitle(getString(R.string.reminder_notification_title)) // "Đến giờ uống thuốc!"
//                .setContentText(getString(R.string.reminder_notification_content, details.getMedicationName(), details.getDosage())) // "Nhấn để xem: [Tên thuốc] ([Liều lượng])"
//                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao
//                .setCategory(NotificationCompat.CATEGORY_ALARM) // Danh mục báo thức
//                .setAutoCancel(true) // Tự hủy khi nhấn
//                .setOngoing(true) // Nên là ongoing khi là foreground/fullscreen
//                .setSound(alarmSound) // Âm thanh
//                .setVibrate(new long[]{0, 500, 100, 500}) // Rung: delay, vibrate, sleep, vibrate
//                .setContentIntent(fullScreenPendingIntent) // Hành động khi nhấn
//                // *** Quan trọng: Full Screen Intent ***
//                .setFullScreenIntent(fullScreenPendingIntent, true); // true = high priority
//
//        // TODO: Thêm Action Buttons nếu muốn (ví dụ: Đã uống, Bỏ qua)
//        // .addAction(R.drawable.ic_check, getString(R.string.action_taken), createActionPendingIntent(...))
//
//        return builder.build();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_HIGH// Quan trọng: HIGH cho full screen
//            );
//            channel.setDescription("Kênh thông báo nhắc nhở uống thuốc");
//            channel.enableLights(true);
//            channel.setLightColor(android.graphics.Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{0, 500, 100, 500});
//
//            // Đặt thuộc tính âm thanh cho kênh (quan trọng cho Android 8+)
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_ALARM) // Dùng ALARM usage
//                    .build();
//            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes);
//            channel.setBypassDnd(true); // Bỏ qua chế độ Không làm phiền
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // Hiển thị trên màn hình khóa
//
//            this.notificationManager.createNotificationChannel(channel);
//
//            Log.i(TAG, "Notification channel created.");
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "Service destroyed.");
//        // Dọn dẹp nếu cần (ví dụ: hủy coroutine scope nếu tự tạo)
//    }
//}