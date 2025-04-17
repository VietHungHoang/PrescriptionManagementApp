//package com.mad.prescriptionmanagementapp.ui.activity;
//
//import android.app.KeyguardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Vibrator;
//import android.util.Log;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.databinding.DataBindingUtil;
//import androidx.lifecycle.ViewModelProvider;
//
//
//import com.mad.prescriptionmanagementapp.R;
//import com.mad.prescriptionmanagementapp.databinding.ActivityReminderBinding;
//import com.mad.prescriptionmanagementapp.ui.viewmodel.ReminderViewModel;
//
//import java.io.IOException;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint // Quan trọng cho Hilt
//public class ReminderActivity extends AppCompatActivity {
//
//    private static final String TAG = "ReminderActivity";
//    public static final String EXTRA_SCHEDULE_ID = "com.yourcompany.yourapp.EXTRA_SCHEDULE_ID";
//    private static final long[] VIBRATION_PATTERN = {0, 500, 200, 500}; // Rung
//
//    private ActivityReminderBinding binding;
//    private ReminderViewModel viewModel;
//    private MediaPlayer mediaPlayer;
//    private Vibrator vibrator;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate");
//
//        // ----- Thiết lập Window Flags -----
//        // Hiển thị trên màn hình khóa và bật màn hình
//        this.setShowWhenLocked(true);
//        this.setTurnScreenOn(true);
//
//        // Bỏ qua Keyguard (nếu đang khóa)
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        if (keyguardManager != null) {
//            keyguardManager.requestDismissKeyguard(this, null);
//        }
//
//        // Khởi tạo Data Binding và ViewModel
//        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
//        this.viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
//
//        // Gán ViewModel và LifecycleOwner cho Binding
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(this);
//
//        // Lấy scheduleId từ Intent
//        long scheduleId = getIntent().getLongExtra(EXTRA_SCHEDULE_ID, -1L);
//        Log.d(TAG, "Received scheduleId: " + scheduleId);
//
//        // Tải dữ liệu
//        if (scheduleId != -1L) {
//            viewModel.loadReminderDetails(scheduleId);
//        } else {
//            Log.e(TAG, "Invalid scheduleId received in Intent. Finishing activity.");
//            Toast.makeText(this, R.string.reminder_error_invalid_id, Toast.LENGTH_SHORT).show();
//            finishWithError(); // Đóng Activity và dừng service/âm thanh
//            return; // Quan trọng: Dừng thực thi onCreate ở đây
//        }
//
//        // Lắng nghe sự kiện đóng màn hình từ ViewModel
//        viewModel.closeScreenEvent.observe(this, unused -> {
//            Log.d(TAG, "Close screen event received.");
//            finishAndCleanup();
//        });
//
//        // Lắng nghe lỗi từ ViewModel (tùy chọn)
//        viewModel.errorMessage.observe(this, errorMsg -> {
//            if (errorMsg != null && !errorMsg.isEmpty()) {
//                Log.e(TAG, "Error from ViewModel: " + errorMsg);
//                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
//                // Có thể finish() nếu là lỗi nghiêm trọng
//            }
//        });
//
//        // Khởi tạo và bắt đầu phát âm thanh + rung
//        setupSoundAndVibration();
//        startSoundAndVibration();
//    }
//
//    private void setupSoundAndVibration() {
//        // Setup âm thanh
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioAttributes(new android.media.AudioAttributes.Builder()
//                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
//                .build());
//        mediaPlayer.setLooping(true); // Lặp lại âm báo
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmSound == null) {
//            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//
//        try {
//            if (alarmSound != null) {
//                mediaPlayer.setDataSource(this, alarmSound);
//                mediaPlayer.prepareAsync(); // Chuẩn bị bất đồng bộ
//                mediaPlayer.setOnPreparedListener(mp -> Log.d(TAG, "MediaPlayer prepared"));
//                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
//                    Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
//                    return true; // Đã xử lý lỗi
//                });
//            } else {
//                Log.w(TAG, "Default alarm sound not found.");
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Error setting up MediaPlayer data source", e);
//            mediaPlayer = null; // Đặt lại nếu có lỗi
//        }
//
//        // Setup rung
//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//    }
//
//    private void startSoundAndVibration() {
//        // Phát âm thanh khi đã chuẩn bị xong
//        if (mediaPlayer != null) {
//            mediaPlayer.setOnPreparedListener(mp -> {
//                Log.i(TAG, "Starting MediaPlayer.");
//                try {
//                    if (!mp.isPlaying()) {
//                        mp.start();
//                    }
//                } catch (IllegalStateException e) {
//                    Log.e(TAG, "Error starting MediaPlayer", e);
//                }
//            });
//            // Nếu đã prepare rồi thì start luôn
//            if (mediaPlayer.isLooping() && !mediaPlayer.isPlaying()) { // Kiểm tra isLooping để đoán là đã prepare
//                try {
//                    Log.i(TAG, "MediaPlayer already prepared, starting now.");
//                    mediaPlayer.start();
//                } catch (IllegalStateException e) {
//                    Log.e(TAG, "Error starting prepared MediaPlayer", e);
//                }
//            }
//        }
//
//        // Bắt đầu rung
//        if (vibrator != null && vibrator.hasVibrator()) {
//            Log.i(TAG, "Starting vibration.");
//            // -1: không lặp lại, 0: lặp lại từ đầu pattern
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(android.os.VibrationEffect.createWaveform(VIBRATION_PATTERN, 0));
//            } else {
//                // Deprecated in API 26
//                vibrator.vibrate(VIBRATION_PATTERN, 0);
//            }
//        }
//    }
//
//    private void stopSoundAndVibration() {
//        Log.d(TAG, "Stopping sound and vibration.");
//        // Dừng âm thanh
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            try {
//                mediaPlayer.stop();
//            } catch (IllegalStateException e) {
//                Log.e(TAG, "Error stopping MediaPlayer", e);
//            }
//        }
//        // Giải phóng MediaPlayer
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//
//        // Dừng rung
//        if (vibrator != null) {
//            vibrator.cancel();
//        }
//    }
//
//    private void stopReminderService() {
//        Log.d(TAG, "Stopping ReminderNotificationService.");
//        Intent serviceIntent = new Intent(this, ReminderNotificationService.class);
//        stopService(serviceIntent);
//        // Dừng foreground và hủy notification nếu service chưa tự dừng
//        // Bạn cũng có thể gửi một action tùy chỉnh đến service để nó tự stopForeground và stopSelf
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        long scheduleId = getIntent().getLongExtra(EXTRA_SCHEDULE_ID, -1L);
//        if (scheduleId != -1L) {
//            notificationManager.cancel(ReminderNotificationService.NOTIFICATION_ID_BASE + (int) scheduleId);
//        }
//    }
//
//    /**
//     * Dọn dẹp và đóng Activity một cách an toàn.
//     */
//    private void finishAndCleanup() {
//        stopSoundAndVibration();
//        stopReminderService();
//        finish();
//        Log.i(TAG, "Activity finished and cleaned up.");
//    }
//
//    /**
//     * Đóng activity khi có lỗi khởi tạo.
//     */
//    private void finishWithError() {
//        stopSoundAndVibration();
//        stopReminderService();
//        finish();
//        Log.e(TAG, "Activity finished due to an error.");
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        Log.d(TAG, "onDestroy");
//        stopSoundAndVibration(); // Đảm bảo dừng nếu Activity bị hủy đột ngột
//        // Không cần gọi stopReminderService ở đây nữa vì finishAndCleanup đã gọi
//        super.onDestroy();
//        // Hủy binding để tránh memory leak (không cần thiết nếu dùng Fragment)
//        binding = null;
//    }
//
//    // Ghi đè nút back để đảm bảo cleanup (tùy chọn, nhưng nên làm)
//    @Override
//    public void onBackPressed() {
//        Log.d(TAG, "onBackPressed called. Finishing activity.");
//        finishAndCleanup();
//        // Không gọi super.onBackPressed() vì chúng ta đã xử lý finish
//    }
//}