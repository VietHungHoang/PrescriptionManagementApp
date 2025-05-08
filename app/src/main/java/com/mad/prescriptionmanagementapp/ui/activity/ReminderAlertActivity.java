package com.mad.prescriptionmanagementapp.ui.activity;

// com.yourpackage.ui.reminder.ReminderAlertActivity.java
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Sử dụng AndroidViewModelFactory

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.receiver.AlarmReceiver;
import com.mad.prescriptionmanagementapp.ui.viewmodel.ReminderViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderAlertActivity extends AppCompatActivity {

    private static final String TAG = "ReminderAlertActivity";
    public static final String EXTRA_REMINDER_ID_ALERT = "com.yourpackage.EXTRA_REMINDER_ID_ALERT";
    public static final String EXTRA_DRUG_NAME_ALERT = "com.yourpackage.EXTRA_DRUG_NAME_ALERT";
    public static final String EXTRA_DOSAGE_INFO_ALERT = "com.yourpackage.EXTRA_DOSAGE_INFO_ALERT";
    private static final String ACTION_CLOSE_REMINDER_ALERT_ACTIVITY = "com.yourpackage.ACTION_CLOSE_REMINDER_ALERT";


    private TextView tvTime, tvReminderTitle, tvDrugName, tvDosageInfo;
    private Button btnTaken, btnSnooze, btnSkip;

    private ReminderViewModel reminderViewModel;
    private long currentReminderId = -1;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private BroadcastReceiver closeActivityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập cờ để hiển thị trên màn hình khóa và bật màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        setContentView(R.layout.activity_reminder_alert);

        tvTime = findViewById(R.id.tvTime);
        tvReminderTitle = findViewById(R.id.tvReminderTitle);
        tvDrugName = findViewById(R.id.tvDrugName);
        tvDosageInfo = findViewById(R.id.tvDosageInfo);
        btnTaken = findViewById(R.id.btnTaken);
        btnSnooze = findViewById(R.id.btnSnooze);
        btnSkip = findViewById(R.id.btnSkip);

        // Khởi tạo ViewModel
        reminderViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ReminderViewModel.class);


        Intent intent = getIntent();
        if (intent != null) {
            currentReminderId = intent.getLongExtra(EXTRA_REMINDER_ID_ALERT, -1);
            String drugName = intent.getStringExtra(EXTRA_DRUG_NAME_ALERT);
            String dosageInfo = intent.getStringExtra(EXTRA_DOSAGE_INFO_ALERT);

            if (currentReminderId != -1) {
                // Hiển thị thông tin thuốc (có thể load lại từ ViewModel nếu cần thông tin đầy đủ hơn)
                // reminderViewModel.loadReminderDetail(currentReminderId); // Nếu bạn có LiveData cho chi tiết
                // reminderViewModel.getCurrentReminderDetail().observe(this, reminderDetail -> {
                //    if (reminderDetail != null && reminderDetail.drugInfo != null) {
                //        tvDrugName.setText(reminderDetail.drugInfo.name);
                //        tvDosageInfo.setText(reminderDetail.reminderInstance.dosageToTake + " " + reminderDetail.reminderInstance.unitNameForDisplay);
                //    }
                // });
                // Tạm thời dùng dữ liệu truyền qua intent
                if (drugName != null) tvDrugName.setText(drugName);
                if (dosageInfo != null) tvDosageInfo.setText(dosageInfo);
            } else {
                Log.e(TAG, "No reminder ID passed to ReminderAlertActivity. Finishing.");
                finish();
                return;
            }
        } else {
            Log.e(TAG, "Intent is null. Finishing.");
            finish();
            return;
        }

        // Cập nhật thời gian hiện tại
        updateCurrentTime();
        timeRunnable = this::updateCurrentTime;
        timeHandler.postDelayed(timeRunnable, 1000); // Cập nhật mỗi giây


        btnTaken.setOnClickListener(v -> handleAction(AlarmReceiver.ACTION_TAKE));
        btnSnooze.setOnClickListener(v -> handleAction(AlarmReceiver.ACTION_SNOOZE));
        btnSkip.setOnClickListener(v -> handleAction(AlarmReceiver.ACTION_SKIP));

        startAlarmSoundAndVibration();

        // Đăng ký BroadcastReceiver để đóng Activity
        closeActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_CLOSE_REMINDER_ALERT_ACTIVITY.equals(intent.getAction())) {
                    long reminderIdToClose = intent.getLongExtra("reminder_id_to_close", -1);
                    if (reminderIdToClose == currentReminderId || reminderIdToClose == -1) { // -1 để đóng mọi instance
                        Log.d(TAG, "Received close broadcast. Finishing ReminderAlertActivity for ID: " + currentReminderId);
                        finishAndStopAlarm();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_CLOSE_REMINDER_ALERT_ACTIVITY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(closeActivityReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
//            registerReceiver(closeActivityReceiver, filter);
        }
    }

    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault()); // "hh:mm a" cho AM/PM
        tvTime.setText(sdf.format(new Date()));
        timeHandler.postDelayed(timeRunnable, 1000); // Lặp lại
    }


    private void handleAction(String action) {
        if (currentReminderId == -1) return;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int) currentReminderId); // Hủy notification nếu có

        switch (action) {
            case AlarmReceiver.ACTION_TAKE:
                reminderViewModel.markAsTaken(currentReminderId);
                Toast.makeText(this, "Đã ghi nhận uống thuốc!", Toast.LENGTH_SHORT).show();
                break;
            case AlarmReceiver.ACTION_SNOOZE:
                reminderViewModel.snoozeReminder(currentReminderId, 15); // Snooze 15 phút
                Toast.makeText(this, "Sẽ nhắc lại sau 15 phút.", Toast.LENGTH_SHORT).show();
                break;
            case AlarmReceiver.ACTION_SKIP:
                reminderViewModel.skipReminder(currentReminderId);
                Toast.makeText(this, "Đã bỏ qua lần uống thuốc này.", Toast.LENGTH_SHORT).show();
                break;
        }
        finishAndStopAlarm();
    }

    private void startAlarmSoundAndVibration() {
        // Sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        mediaPlayer = MediaPlayer.create(this, alarmSound);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 1000}; // Chờ 0ms, rung 1000ms, nghỉ 1000ms
            // Lặp lại pattern (index 0 là không lặp, 1 là lặp từ pattern[1])
            // API 26+ dùng VibrationEffect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                android.os.VibrationEffect effect = android.os.VibrationEffect.createWaveform(pattern, 0);
                vibrator.vibrate(effect);
            } else {
                //noinspection deprecation
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    private void stopAlarmSoundAndVibration() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    private void finishAndStopAlarm() {
        stopAlarmSoundAndVibration();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(timeRunnable); // Dọn dẹp handler
        stopAlarmSoundAndVibration(); // Đảm bảo âm thanh và rung dừng lại
        if (closeActivityReceiver != null) {
            unregisterReceiver(closeActivityReceiver);
        }
        Log.d(TAG, "ReminderAlertActivity onDestroy for ID: " + currentReminderId);
    }

    // Để ngăn người dùng nhấn back thoát khỏi màn hình báo thức mà không xử lý
//    @Override
//    public void onBackPressed() {
//        // super.onBackPressed(); // Không gọi super để vô hiệu hóa nút back
//        Toast.makeText(this, "Vui lòng chọn một hành động.", Toast.LENGTH_SHORT).show();
//    }
}