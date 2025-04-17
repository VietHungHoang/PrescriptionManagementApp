//package com.mad.prescriptionmanagementapp.scheduler;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri; // Thêm Uri nếu dùng data cho intent
//import android.os.Build;
//import android.util.Log;
//
//import com.yourcompany.yourapp.data.db.entity.ScheduleEntity; // Cần Entity để tính next trigger
//import com.yourcompany.yourapp.domain.repository.ScheduleRepository; // Cần Repo để lấy lịch trình
//import com.yourcompany.yourapp.domain.scheduler.AlarmScheduler;
//import com.yourcompany.yourapp.receiver.AlarmReceiver; // BroadcastReceiver
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.Executor; // Dùng Executor cho tác vụ nền
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//import dagger.hilt.android.qualifiers.ApplicationContext;
//
//@Singleton
//public class AlarmSchedulerImpl implements AlarmScheduler {
//
//    private static final String TAG = "AlarmSchedulerImpl";
//    public static final String EXTRA_SCHEDULE_ID = "com.yourcompany.yourapp.EXTRA_SCHEDULE_ID";
//    public static final String EXTRA_MEDICATION_ID = "com.yourcompany.yourapp.EXTRA_MEDICATION_ID";
//    public static final String ACTION_REMINDER_ALARM = "com.yourcompany.yourapp.REMINDER_ALARM";
//    private static final int ALARM_REQUEST_CODE_BASE = 1000; // Base cho request code
//
//    private final Context context;
//    private final AlarmManager alarmManager;
//    private final ScheduleRepository scheduleRepository; // Inject repo lấy lịch trình
//    private final Executor ioExecutor; // Inject executor để chạy lấy dữ liệu DB
//
//    @Inject
//    public AlarmSchedulerImpl(
//            @ApplicationContext Context context,
//            AlarmManager alarmManager,
//            ScheduleRepository scheduleRepository,
//            Executor ioExecutor) {
//        this.context = context;
//        this.alarmManager = alarmManager;
//        this.scheduleRepository = scheduleRepository;
//        this.ioExecutor = ioExecutor;
//    }
//
//    @Override
//    public void scheduleReminder(long scheduleId, Long medicationId, long triggerTimeMillis) {
//        // Kiểm tra quyền trên Android 12+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (!this.alarmManager.canScheduleExactAlarms()) {
//                Log.w(TAG, "Cannot schedule exact alarms. Need SCHEDULE_EXACT_ALARM permission.");
//                // TODO: Xử lý trường hợp không có quyền (ví dụ: thông báo user, dùng báo thức không chính xác)
//                // alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, createPendingIntent(scheduleId, medicationId, PendingIntent.FLAG_UPDATE_CURRENT));
//                return; // Hoặc ném lỗi, hoặc dùng cách khác
//            }
//        }
//
//        PendingIntent pendingIntent = createPendingIntent(scheduleId, medicationId, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        try {
//            alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTimeMillis,
//                    pendingIntent
//            );
//            Log.i(TAG, "Scheduled exact alarm for schedule " + scheduleId + " at " + new Date(triggerTimeMillis));
//        } catch (SecurityException se) {
//            Log.e(TAG, "SecurityException: Possibly missing SCHEDULE_EXACT_ALARM or related permission.", se);
//            // Xử lý lỗi thiếu quyền
//        } catch (Exception e) {
//            Log.e(TAG, "Error scheduling alarm for schedule " + scheduleId, e);
//        }
//    }
//
//    @Override
//    public void scheduleSnooze(long scheduleId, Long medicationId) {
//        scheduleSnooze(scheduleId, medicationId, 10); // Mặc định 10 phút
//    }
//
//    @Override
//    public void scheduleSnooze(long scheduleId, Long medicationId, int snoozeMinutes) {
//        long triggerTimeMillis = System.currentTimeMillis() + (long) snoozeMinutes * 60 * 1000;
//        Log.i(TAG, "Scheduling snooze for schedule " + scheduleId + " in " + snoozeMinutes + " minutes.");
//        scheduleReminder(scheduleId, medicationId, triggerTimeMillis);
//    }
//
//    @Override
//    public void cancelAlarm(long scheduleId) {
//        // Tạo lại PendingIntent giống hệt lúc đặt, nhưng dùng FLAG_NO_CREATE
//        PendingIntent pendingIntent = createPendingIntent(scheduleId, null, PendingIntent.FLAG_NO_CREATE);
//        if (pendingIntent != null) {
//            alarmManager.cancel(pendingIntent);
//            pendingIntent.cancel(); // Hủy cả PendingIntent
//            Log.i(TAG, "Cancelled alarm for schedule " + scheduleId);
//        } else {
//            Log.w(TAG, "PendingIntent for schedule " + scheduleId + " not found for cancellation, maybe already cancelled or never set.");
//        }
//    }
//
//    @Override
//    public void rescheduleAllActiveAlarms() {
//        // Chạy tác vụ lấy dữ liệu và đặt lịch trên background thread
//        ioExecutor.execute(() -> {
//            try {
//                // Lấy danh sách lịch trình đang hoạt động từ DB (cần hàm blocking trong repo/dao)
//                List<ScheduleEntity> activeSchedules = scheduleRepository.getAllActiveSchedulesBlocking(); // Cần hàm này
//
//                if (activeSchedules == null || activeSchedules.isEmpty()) {
//                    Log.i(TAG, "No active schedules to reschedule after boot.");
//                    return;
//                }
//
//                Log.i(TAG, "Rescheduling " + activeSchedules.size() + " alarms after boot...");
//                long now = System.currentTimeMillis();
//
//                for (ScheduleEntity schedule : activeSchedules) {
//                    // Tính toán thời gian nhắc nhở TIẾP THEO
//                    Long nextTriggerTime = calculateNextTriggerTime(schedule, now); // Hàm quan trọng
//
//                    if (nextTriggerTime != null && nextTriggerTime > now) {
//                        Log.d(TAG, "Rescheduling for " + schedule.getId() + " at " + new Date(nextTriggerTime));
//                        scheduleReminder(schedule.getId(), schedule.getMedicationId(), nextTriggerTime);
//                    } else {
//                        Log.d(TAG, "Schedule " + schedule.getId() + " next trigger time is in the past or null. Skipping reschedule.");
//                        // Có thể cần hủy báo thức cũ nếu logic tính toán phức tạp
//                        // cancelAlarm(schedule.getId());
//                    }
//                }
//                Log.i(TAG, "Finished rescheduling alarms.");
//
//            } catch (Exception e) {
//                Log.e(TAG, "Error rescheduling alarms after boot", e);
//            }
//        });
//    }
//
//    // Tạo PendingIntent
//    private PendingIntent createPendingIntent(long scheduleId, Long medicationId, int flag) {
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.setAction(ACTION_REMINDER_ALARM); // Đặt Action
//        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
//        if (medicationId != null) {
//            intent.putExtra(EXTRA_MEDICATION_ID, medicationId);
//        }
//        // Đảm bảo intent khác nhau nếu cần phân biệt chi tiết hơn (ví dụ: bằng data)
//        // intent.setData(Uri.parse("schedule:" + scheduleId));
//
//        // Request code phải duy nhất cho mỗi báo thức cần quản lý riêng biệt
//        int requestCode = ALARM_REQUEST_CODE_BASE + (int) scheduleId;
//
//        // Xác định Mutability Flag
//        int mutabilityFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? PendingIntent.FLAG_IMMUTABLE : 0;
//        // Nếu bạn cần thay đổi intent sau khi tạo PendingIntent (ít phổ biến hơn), dùng FLAG_MUTABLE
//        // int mutabilityFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? PendingIntent.FLAG_MUTABLE : 0;
//
//
//        return PendingIntent.getBroadcast(
//                context,
//                requestCode,
//                intent,
//                flag | mutabilityFlag // Kết hợp flag truyền vào và mutability flag
//        );
//    }
//
//    // *** Hàm quan trọng: Tính toán thời gian nhắc nhở tiếp theo ***
//    // Ví dụ đơn giản cho lịch hàng ngày
//    private Long calculateNextTriggerTime(ScheduleEntity schedule, long currentTimeMillis) {
//        // Giả sử ScheduleEntity có getHourOfDay() và getMinuteOfHour()
//        if (schedule.getHourOfDay() == null || schedule.getMinuteOfHour() == null) return null;
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(currentTimeMillis);
//        calendar.set(Calendar.HOUR_OF_DAY, schedule.getHourOfDay());
//        calendar.set(Calendar.MINUTE, schedule.getMinuteOfHour());
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        // Nếu giờ đã qua trong ngày hôm nay, đặt cho ngày mai
//        if (calendar.getTimeInMillis() <= currentTimeMillis) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//        }
//
//        // TODO: Xử lý logic phức tạp hơn: ngày bắt đầu/kết thúc, cách ngày, theo tuần, etc.
//        // TODO: Kiểm tra xem lịch trình có còn active không.
//
//        return calendar.getTimeInMillis();
//    }
//}