package com.mad.prescriptionmanagementapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mad.prescriptionmanagementapp.ui.viewmodel.ReminderViewModel;

// com.yourpackage.receiver.NotificationActionReceiver.java

import android.app.NotificationManager;
import android.util.Log;

// Hoặc có thể gọi trực tiếp Repository/AlarmScheduler
// Nhưng ViewModel là cách tiếp cận MVVM tốt hơn

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationActionRcvr";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        long reminderId = intent.getLongExtra(AlarmReceiver.EXTRA_REMINDER_ID, -1);

        if (reminderId == -1 || action == null) {
            Log.e(TAG, "Invalid action or reminder ID.");
            return;
        }

        Log.d(TAG, "Action: " + action + " for reminder ID: " + reminderId);

        // Hủy notification sau khi action được thực hiện
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int) reminderId); // ID của notification giống với reminderId

        // Lấy ReminderViewModel để xử lý
        // Cách lấy ViewModel ở đây hơi phức tạp vì Receiver không có LifecycleOwner.
        // Cách 1: Truyền Application context để tạo ViewModel (như trong ví dụ này).
        // Cách 2: Dùng một Service trung gian mà ViewModel có thể tương tác.
        // Cách 3: Gọi trực tiếp Repository và AlarmScheduler (ít tuân thủ MVVM hơn).
        ReminderViewModel reminderViewModel = new ReminderViewModel((android.app.Application) context.getApplicationContext());

        switch (action) {
            case AlarmReceiver.ACTION_TAKE:
                Log.i(TAG, "Marking reminder " + reminderId + " as TAKEN.");
                reminderViewModel.markAsTaken(reminderId);
                Toast.makeText(context, "Đã ghi nhận uống thuốc!", Toast.LENGTH_SHORT).show();
                // Đóng ReminderAlertActivity nếu đang mở
                closeReminderAlertActivity(context, reminderId);
                break;
            case AlarmReceiver.ACTION_SNOOZE:
                Log.i(TAG, "Snoozing reminder " + reminderId + " for 15 minutes.");
                reminderViewModel.snoozeReminder(reminderId, 15); // Snooze 15 phút
                Toast.makeText(context, "Sẽ nhắc lại sau 15 phút.", Toast.LENGTH_SHORT).show();
                closeReminderAlertActivity(context, reminderId);
                break;
            case AlarmReceiver.ACTION_SKIP:
                Log.i(TAG, "Skipping reminder " + reminderId + ".");
                reminderViewModel.skipReminder(reminderId);
                Toast.makeText(context, "Đã bỏ qua lần uống thuốc này.", Toast.LENGTH_SHORT).show();
                closeReminderAlertActivity(context, reminderId);
                break;
            default:
                Log.w(TAG, "Unknown action: " + action);
                break;
        }
    }

    private void closeReminderAlertActivity(Context context, long reminderId) {
        Intent closeIntent = new Intent("com.yourpackage.ACTION_CLOSE_REMINDER_ALERT");
        closeIntent.putExtra("reminder_id_to_close", reminderId);
        closeIntent.setPackage(context.getPackageName()); // Đảm bảo chỉ app của bạn nhận được
        context.sendBroadcast(closeIntent);
        Log.d(TAG, "Sent broadcast to close ReminderAlertActivity for ID: " + reminderId);
    }
}

