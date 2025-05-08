package com.mad.prescriptionmanagementapp.receiver;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.repository.MedicationRepository;
import com.mad.prescriptionmanagementapp.ui.activity.ReminderAlertActivity;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String EXTRA_REMINDER_ID = "com.yourpackage.EXTRA_REMINDER_ID";
    public static final String ACTION_TRIGGER_REMINDER = "com.yourpackage.ACTION_TRIGGER_REMINDER";

    // Actions cho notification buttons
    public static final String ACTION_TAKE = "com.yourpackage.ACTION_TAKE";
    public static final String ACTION_SNOOZE = "com.yourpackage.ACTION_SNOOZE";
    public static final String ACTION_SKIP = "com.yourpackage.ACTION_SKIP";

    public static final String NOTIFICATION_CHANNEL_ID = "medication_reminder_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Medication Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received! Action: " + intent.getAction());

        long reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1);
        if (reminderId == -1) {
            Log.e(TAG, "No reminder ID found in intent.");
            return;
        }

        Log.d(TAG, "Processing reminder ID: " + reminderId);

        // Lấy thông tin reminder từ DB (nên thực hiện trên background thread)
        // Tuy nhiên, onReceive() có giới hạn thời gian, nên cần nhanh chóng.
        // Nếu query DB quá lâu, cân nhắc dùng IntentService hoặc JobIntentService.
        // Hoặc, Repository có thể cung cấp LiveData và chúng ta chỉ trigger UI.
        // Đối với full screen intent, chúng ta cần dữ liệu ngay.
        MedicationRepository repository = new MedicationRepository((android.app.Application) context.getApplicationContext());

        // Việc lấy dữ liệu DB trực tiếp trong onReceive không phải là best practice
        // nếu nó mất nhiều thời gian. Cân nhắc dùng một service trung gian.
        // Tạm thời, để đơn giản, ta lấy trực tiếp.
        // Chạy trên một thread tạm thời để tránh ANR, nhưng vẫn cần nhanh.
        new Thread(() -> {
            // ReminderInstanceWithDrug cung cấp cả thông tin thuốc
            ScheduleEntity reminder = repository.getReminderInstanceByIdSync(reminderId);
            // Để lấy tên thuốc, cần join. Giả sử bạn có hàm getReminderInstanceWithDrugByIdSync trong repo
            // ReminderInstanceWithDrug reminderWithDrug = repository.getReminderInstanceWithDrugByIdSync(reminderId);

            if (reminder == null /*|| reminderWithDrug == null*/) {
                Log.e(TAG, "Reminder not found in DB for ID: " + reminderId);
                return;
            }

            // Nếu reminder đã được xử lý (TAKEN, SKIPPED) thì không hiển thị nữa
            if (!reminder.status.equals(ScheduleEntity.STATUS_PENDING) &&
                    !reminder.status.equals(ScheduleEntity.STATUS_SNOOZED)) {
                Log.w(TAG, "Reminder ID " + reminderId + " already processed (status: " + reminder.status + "). Skipping notification.");
                return;
            }

            // Để lấy tên thuốc, chúng ta cần thông tin từ DrugEntity.
            // Giả sử MedicationRepository có một hàm để lấy chi tiết này:
            // DrugEntity drug = repository.getDrugByIdSync(reminder.drugId);
            // String drugName = (drug != null) ? drug.name : "Thuốc";
            // String dosageInfo = reminder.dosageToTake + " " + reminder.unitNameForDisplay;
            // Vì đang dùng ReminderInstanceEntity, thông tin thuốc sẽ phải query riêng hoặc đã có trong ReminderInstanceEntity

            // Tạm thời dùng thông tin từ ReminderInstanceEntity (cần bổ sung tên thuốc vào đây)
            // Hoặc dùng một POJO kết hợp như ReminderInstanceWithDrug

            String drugName = "Thuốc ID " + reminder.drugId; // Cần lấy tên thuốc thực sự
            String dosageInfo = reminder.dosageToTake + " " + reminder.unitNameForDisplay;
            String contentText = "Đã đến giờ uống " + drugName + " (" + dosageInfo + ")";


            // 1. Tạo Notification
            createNotificationChannel(context);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Intent khi nhấn vào notification (mở màn hình báo thức)
            Intent fullScreenIntent = new Intent(context, ReminderAlertActivity.class);
            fullScreenIntent.putExtra(ReminderAlertActivity.EXTRA_REMINDER_ID_ALERT, reminderId);
            fullScreenIntent.putExtra(ReminderAlertActivity.EXTRA_DRUG_NAME_ALERT, drugName); // Truyền thêm thông tin
            fullScreenIntent.putExtra(ReminderAlertActivity.EXTRA_DOSAGE_INFO_ALERT, dosageInfo);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context,
                    (int) (System.currentTimeMillis() & 0xfffffff), // Request code khác nhau
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Action Buttons
            // "Đã uống" action
            Intent takeIntent = new Intent(context, NotificationActionReceiver.class);
            takeIntent.setAction(ACTION_TAKE);
            takeIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
            PendingIntent takePendingIntent = PendingIntent.getBroadcast(context,
                    (int) reminderId + 1, takeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // "Uống sau 15p" action
            Intent snoozeIntent = new Intent(context, NotificationActionReceiver.class);
            snoozeIntent.setAction(ACTION_SNOOZE);
            snoozeIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context,
                    (int) reminderId + 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // "Bỏ qua" action
            Intent skipIntent = new Intent(context, NotificationActionReceiver.class);
            skipIntent.setAction(ACTION_SKIP);
            skipIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
            PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context,
                    (int) reminderId + 3, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification_icon) // Thay bằng icon của bạn
                            .setContentTitle("Nhắc nhở uống thuốc")
                            .setContentText(contentText)
                            .setPriority(NotificationCompat.PRIORITY_HIGH) // Quan trọng cho heads-up
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setAutoCancel(true) // Tự hủy khi nhấn
                            .setSound(alarmSound)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) // Rung
                            .setLights(Color.RED, 3000, 3000) // Đèn LED
                            .setFullScreenIntent(fullScreenPendingIntent, true) // Quan trọng để hiển thị toàn màn hình
                            .addAction(R.drawable.ic_action_taken, "Đã uống", takePendingIntent)
                            .addAction(R.drawable.ic_action_snooze, "Uống sau 15p", snoozePendingIntent)
                            .addAction(R.drawable.ic_action_skip, "Bỏ qua", skipPendingIntent);


            // Hiển thị notification. ID của notification nên là duy nhất cho mỗi reminder.
            // Để đơn giản, có thể dùng reminderId (cast sang int)
            notificationManager.notify((int) reminderId, notificationBuilder.build());
            Log.d(TAG, "Notification shown for reminder ID: " + reminderId);

            // 2. (TÙY CHỌN) Mở trực tiếp Activity toàn màn hình thay vì chỉ notification
            //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //        // fullScreenIntent đã được thiết lập ở trên
            //        context.startActivity(fullScreenIntent);
            //        Log.d(TAG, "Started ReminderAlertActivity directly for reminder ID: " + reminderId);
            //    } else {
            //        // Trên các phiên bản cũ hơn, setFullScreenIntent trong notification là đủ
            //    }
            // Thông thường, setFullScreenIntent là đủ. Việc startActivity trực tiếp ở đây có thể không cần thiết.

        }).start(); // Chạy trên thread mới
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // Đặt IMPORTANCE_HIGH để heads-up và full screen intent hoạt động tốt
            );
            channel.setDescription("Channel for medication reminder alarms");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            // channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            //        new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            // Đảm bảo âm thanh báo thức được ưu tiên

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created.");
            }
        }
    }
}