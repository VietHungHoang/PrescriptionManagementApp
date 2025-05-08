//package com.mad.prescriptionmanagementapp.util;
//
//// com.yourpackage.util.AlarmScheduler.java
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
//
//import com.mad.prescriptionmanagementapp.data.repository.MedicationRepository;
//import com.mad.prescriptionmanagementapp.receiver.AlarmReceiver;
//
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class AlarmScheduler {
//    private static final String TAG = "AlarmScheduler";
//    private Context context;
//    private AlarmManager alarmManager;
//    private MedicationRepository repository; // Để lấy thông tin reminder
//    private ExecutorService executorService;
//
//
//    public AlarmScheduler(Context context) {
//        this.context = context.getApplicationContext();
//        this.alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
//        this.repository = new MedicationRepository((android.app.Application) this.context.getApplicationContext());
//        this.executorService = Executors.newSingleThreadExecutor();
//    }
//
//    public void scheduleAllPendingReminders() {
//        executorService.execute(() -> {
//            List<ScheduleEntity> reminders = repository.getAllPendingOrSnoozedRemindersSync();
//            if (reminders != null && !reminders.isEmpty()) {
//                Log.d(TAG, "Scheduling " + reminders.size() + " pending/snoozed reminders.");
//                for (ScheduleEntity reminder : reminders) {
//                    // Chỉ đặt báo thức nếu thời gian chưa qua
//                    if (reminder.reminderTime > System.currentTimeMillis()) {
//                        scheduleAlarm(reminder.id, reminder.reminderTime);
//                    } else {
//                        // Nếu thời gian đã qua (ví dụ, sau khi reboot, app bị tắt lâu)
//                        // bạn có thể quyết định hiển thị thông báo "missed" ngay lập tức
//                        // hoặc bỏ qua. Ở đây ta chỉ log.
//                        Log.w(TAG, "Skipping past reminder ID: " + reminder.id + " at " + reminder.reminderTime);
//                        // Cân nhắc: Cập nhật trạng thái thành SKIPPED hoặc xử lý khác
//                    }
//                }
//            } else {
//                Log.d(TAG, "No pending/snoozed reminders to schedule.");
//            }
//        });
//    }
//
//    public void scheduleAlarm(long reminderId, long triggerAtMillis) {
//        if (triggerAtMillis <= System.currentTimeMillis()) {
//            Log.w(TAG, "Attempted to schedule alarm in the past for reminderId: " + reminderId + ". Skipping.");
//            // Có thể hiển thị thông báo bị lỡ ở đây nếu cần
//            return;
//        }
//
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminderId);
//        // Action để phân biệt các intent, không bắt buộc nhưng hữu ích
//        intent.setAction(AlarmReceiver.ACTION_TRIGGER_REMINDER + reminderId);
//
//
//        // Request code phải là duy nhất cho mỗi PendingIntent nếu bạn muốn cancel hoặc update riêng lẻ
//        // Sử dụng reminderId (long) cast sang int. Cẩn thận nếu reminderId quá lớn.
//        // Một cách tốt hơn là dùng một mapping nếu reminderId là long > Integer.MAX_VALUE
//        // Hoặc dùng String làm action và không set data/type cho intent.
//        // Ở đây, dùng reminderId làm request code có thể gây xung đột nếu reminderId là số âm hoặc quá lớn.
//        // Nên sử dụng một cơ chế tạo request code an toàn hơn.
//        // Ví dụ đơn giản:
//        int requestCode = (int) (reminderId % Integer.MAX_VALUE);
//        if (requestCode < 0) requestCode *= -1; // Đảm bảo không âm
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                requestCode, // Sử dụng reminderId làm request code
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        if (alarmManager != null) {
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
//                    Log.e(TAG, "Cannot schedule exact alarms. User needs to grant permission.");
//                    // TODO: Hướng dẫn người dùng cấp quyền SCHEDULE_EXACT_ALARM
//                    // Hoặc sử dụng setWindow() hoặc set() như một fallback (ít chính xác hơn)
//                    // alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis, 60000, pendingIntent);
//                    // Hiện tại sẽ bỏ qua nếu không có quyền
//                    return;
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
//                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
//                } else {
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
//                }
//                Log.i(TAG, "Scheduled alarm for reminder ID: " + reminderId + " at " + triggerAtMillis + " (requestCode: " + requestCode + ")");
//            } catch (SecurityException se) {
//                Log.e(TAG, "SecurityException: Missing SCHEDULE_EXACT_ALARM permission?", se);
//                // Xử lý trường hợp không có quyền
//            }
//        }
//    }
//
//    public void cancelAlarm(long reminderId) {
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminderId); // Không bắt buộc nhưng nên có để intent giống lúc tạo
//        intent.setAction(AlarmReceiver.ACTION_TRIGGER_REMINDER + reminderId); // Action phải giống hệt lúc tạo
//
//        int requestCode = (int) (reminderId % Integer.MAX_VALUE);
//        if (requestCode < 0) requestCode *= -1;
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                requestCode,
//                intent,
//                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE để không tạo mới nếu chưa có
//        );
//
//        if (alarmManager != null && pendingIntent != null) {
//            alarmManager.cancel(pendingIntent);
//            pendingIntent.cancel(); // Hủy cả PendingIntent
//            Log.i(TAG, "Cancelled alarm for reminder ID: " + reminderId  + " (requestCode: " + requestCode + ")");
//        } else {
//            Log.w(TAG, "Could not cancel alarm for reminder ID: " + reminderId + ". PendingIntent not found or AlarmManager null.");
//        }
//    }
//
//    public void snoozeReminder(long reminderId, int snoozeMinutes) {
//        // 1. Hủy báo thức hiện tại (nếu có)
//        cancelAlarm(reminderId); // Hủy báo thức gốc hoặc snooze trước đó
//
//        // 2. Tính thời gian snooze mới
//        long snoozeTriggerMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L);
//
//        // 3. Đặt báo thức mới cho thời gian snooze
//        // Tạo một intent mới hoặc dùng lại intent cũ nhưng với request code khác cho snooze
//        // hoặc cập nhật reminder trong DB với trạng thái SNOOZED và reminderTime mới, rồi gọi scheduleAlarm.
//        // Cách tốt hơn: Cập nhật ReminderInstanceEntity trong DB trước (trong ViewModel/Repository)
//        // Sau đó AlarmScheduler chỉ việc đặt báo thức dựa trên thông tin mới.
//        // ViewModel đã cập nhật DB, ở đây ta chỉ cần đặt lại báo thức.
//
//        // Lấy lại thông tin reminder đã được cập nhật trạng thái SNOOZED và thời gian snooze mới
//        // Hoặc đơn giản là đặt báo thức mới với thời gian đã tính
//        // ReminderInstanceEntity snoozedReminder = repository.getReminderInstanceByIdSync(reminderId);
//        // if (snoozedReminder != null && snoozedReminder.status.equals(ReminderInstanceEntity.STATUS_SNOOZED)) {
//        //    snoozedReminder.reminderTime = snoozeTriggerMillis; // Cập nhật thời gian snooze
//        //    repository.updateReminderInstance(snoozedReminder); // Lưu lại DB
//        //    scheduleAlarm(snoozedReminder.id, snoozedReminder.reminderTime);
//        // } else {
//        //    Log.e(TAG, "Failed to snooze, reminder not found or not in SNOOZED state after update: " + reminderId);
//        // }
//        // Đơn giản hơn, vì ViewModel đã cập nhật DB, chỉ cần đặt lại:
//        scheduleAlarm(reminderId, snoozeTriggerMillis);
//        Log.i(TAG, "Snooze alarm scheduled for reminder ID: " + reminderId + " in " + snoozeMinutes + " minutes.");
//    }
//
//    // Được gọi khi người dùng chọn "Đã uống" hoặc "Bỏ qua" để đảm bảo không có snooze alarm nào chạy
//    public void cancelSnooze(long reminderId) {
//        // Logic hủy snooze thực ra đã nằm trong cancelAlarm(reminderId)
//        // vì chúng ta dùng cùng reminderId cho cả báo thức gốc và snooze.
//        // Nếu bạn dùng request code riêng cho snooze, bạn cần hủy nó ở đây.
//        // Hiện tại, cancelAlarm(reminderId) là đủ.
//        cancelAlarm(reminderId);
//        Log.d(TAG, "Ensured snooze is cancelled for reminder ID: " + reminderId);
//    }
//
//    // Gọi hàm này khi một Prescription được thêm/cập nhật
//    public void refreshAlarmsForPrescription(Long prescriptionId) {
//        // TODO:
//        // 1. Lấy tất cả DrugInPres thuộc prescriptionId.
//        // 2. Với mỗi DrugInPres, gọi repository.generateAndSaveReminderInstances(...)
//        //    (Hàm này trong repo đã tự xóa các future pending reminders cũ)
//        // 3. Sau đó gọi lại scheduleAllPendingReminders() để đặt lại toàn bộ.
//        //    Hoặc chỉ schedule cho các reminder mới được tạo/cập nhật của prescription này.
//        //    Cách đơn giản là scheduleAllPendingReminders().
//        Log.d(TAG, "Refreshing alarms, potentially for prescription ID: " + prescriptionId);
//        scheduleAllPendingReminders();
//    }
//
//    // Gọi hàm này khi một Prescription bị xóa
//    public void cancelAlarmsForPrescription(Long prescriptionId) {
//        // TODO:
//        // 1. Lấy tất cả DrugInPres thuộc prescriptionId (cần hàm sync trong DAO/Repo).
//        // 2. Với mỗi DrugInPres, lấy tất cả ReminderInstanceEntity của nó.
//        // 3. Gọi cancelAlarm() cho từng ReminderInstanceEntity.ID đó.
//        //    (Việc xóa ReminderInstanceEntity khỏi DB do cascade delete đã làm)
//        // Cách đơn giản: Sau khi prescription bị xóa, các ReminderInstance liên quan cũng bị xóa.
//        // Gọi scheduleAllPendingReminders() sẽ không đặt lại chúng.
//        // Nhưng các PendingIntent cũ vẫn có thể tồn tại trong AlarmManager nếu không được cancel rõ ràng.
//        // => Cần một cách để lấy các reminder ID đã bị xóa để hủy.
//        // => HOẶC: Khi xóa prescription, TRƯỚC KHI xóa DB, lấy danh sách reminder ID, rồi mới xóa DB, sau đó cancel.
//        // => Hiện tại, việc `prescriptionDao.deletePrescriptionById(prescriptionId)` sẽ cascade xóa các reminder.
//        // => Vấn đề là làm sao để `cancelAlarm` cho những ID đã bị xóa đó.
//        // => Một giải pháp là khi xóa, truyền danh sách ID cần hủy báo thức.
//        // => Cách khác: Khi `scheduleAllPendingReminders()`, nó chỉ đặt cho những cái còn tồn tại.
//        //    Những cái đã bị xóa sẽ không được đặt lại. Nhưng nếu chúng đã được đặt trước đó và chưa kích hoạt,
//        //    chúng vẫn có thể kích hoạt. => Phải cancel rõ ràng.
//        Log.d(TAG, "Cancelling alarms associated with prescription ID: " + prescriptionId + ". This needs careful implementation.");
//        // Một cách đơn giản nhưng có thể hơi "nặng": Hủy tất cả và đặt lại tất cả.
//        // cancelAllAlarms(); // Cần hàm hủy tất cả (khó nếu không có list request code)
//        // scheduleAllPendingReminders();
//        // Cách tốt hơn: Repository khi xóa prescription nên trả về danh sách reminder ID đã bị xóa,
//        // rồi AlarmScheduler dùng danh sách đó để cancel.
//    }
//}