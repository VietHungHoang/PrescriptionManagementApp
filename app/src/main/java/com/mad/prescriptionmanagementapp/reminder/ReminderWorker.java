package com.mad.prescriptionmanagementapp.reminder;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.receiver.NotificationActionReceiver;

import java.util.concurrent.TimeUnit;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        this.createNotificationChannel(this.getApplicationContext());

        // Tạo các PendingIntent cho các nút trong Notification
        PendingIntent donePendingIntent = createPendingIntent("done");
        PendingIntent snoozePendingIntent = createPendingIntent("snooze");

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);

        // Set các PendingIntent vào nút trong layout
        remoteViews.setOnClickPendingIntent(R.id.btn_done, donePendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_snooze, snoozePendingIntent);

        // Tạo Notification với âm thanh
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // Đặt âm thanh mặc định

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
//                .setSmallIcon(R.drawable.ic_pill) // Icon của thông báo
////                .setContentTitle("Đến giờ uống thuốc!")
////                .setContentText("Nhớ uống thuốc để giữ sức khỏe nha 💊")
//                .setCustomContentView(remoteViews) // Áp dụng layout tùy chỉnh
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(false) // Không tự động tắt
//                .setSound(soundUri) // Thêm âm thanh vào thông báo
//                .setOngoing(true); // Thông báo sẽ không tự biến mất
////                .addAction(R.drawable.ic_check, "Đã uống", donePendingIntent) // Nút "Đã uống"
////                .addAction(R.drawable.ic_camera, "Nhắc lại sau", snoozePendingIntent); // Nút "Nhắc lại sau"

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setSmallIcon(R.drawable.ic_pill)
//                .setContentTitle("Đến giờ uống thuốc!")
//                .setContentText("Nhớ uống thuốc để giữ sức khỏe nha 💊")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Đây là thông báo có thể mở rộng. Bạn có thể kéo xuống để thấy thêm thông tin và các nút điều khiển."))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle()
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
//                .addAction(R.drawable.ic_camera, "Đã uống", donePendingIntent)
//                .addAction(R.drawable.ic_check, "Nhắc lại sau", snoozePendingIntent)
                .setAutoCancel(true);

        // Kiểm tra quyền POST_NOTIFICATIONS trước khi hiển thị thông báo
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Nếu đã có quyền, hiển thị thông báo
            NotificationManagerCompat.from(getApplicationContext()).notify(1, builder.build());

            // Lặp lại thông báo sau một khoảng thời gian (ví dụ: 5 phút)
            scheduleNextReminder();
        } else {
            // Nếu chưa có quyền, yêu cầu quyền từ người dùng
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                    100);
        }

        return Result.success();

    }

    private PendingIntent createPendingIntent(String action) {
        Intent intent = new Intent(this.getApplicationContext(), NotificationActionReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel(Context context) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Nhắc uống thuốc",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
    }

    private void scheduleNextReminder() {
        // Lên lịch lại thông báo sau 5 phút
        OneTimeWorkRequest nextReminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(nextReminderRequest);
    }

    // in activity
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_CODE_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Nếu người dùng cấp quyền, hiển thị thông báo
//                NotificationManagerCompat.from(getApplicationContext()).notify(1, builder.build());
//                scheduleNextReminder();
//            } else {
//                // Nếu người dùng từ chối quyền, không thể gửi thông báo
//                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
