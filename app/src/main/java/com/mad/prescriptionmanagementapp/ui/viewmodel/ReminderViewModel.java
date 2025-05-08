package com.mad.prescriptionmanagementapp.ui.viewmodel;

// com.yourpackage.viewmodel.ReminderViewModel.java
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.prescriptionmanagementapp.data.model.relation.ScheduleWithDrug;
import com.mad.prescriptionmanagementapp.data.repository.MedicationRepository;
import com.mad.prescriptionmanagementapp.util.AlarmScheduler;

import java.util.List;

public class ReminderViewModel extends AndroidViewModel {
    private final MedicationRepository repository;
    private final AlarmScheduler alarmScheduler; // Sẽ tạo sau
    private LiveData<List<ScheduleWithDrug>> upcomingReminders;

    // Dùng cho màn hình báo thức, để lấy chi tiết một reminder cụ thể
    private final MutableLiveData<Long> currentReminderId = new MutableLiveData<>();
    private LiveData<ScheduleWithDrug> currentReminderDetail;


    public ReminderViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicationRepository(application);
        alarmScheduler = new AlarmScheduler(application); // Sẽ tạo sau

        // Lấy các reminder từ bây giờ trở đi
        upcomingReminders = repository.getUpcomingReminders(System.currentTimeMillis());

        // LiveData này sẽ được trigger khi currentReminderId thay đổi
        // currentReminderDetail = Transformations.switchMap(currentReminderId, id -> {
        //    if (id == null) {
        //        return new MutableLiveData<>(null); // Trả về null nếu không có ID
        //    }
        //    return repository.getReminderInstanceWithDrugById(id); // Giả sử hàm này có trong repo
        // });
        // Tạm thời dùng cách khác nếu getReminderInstanceWithDrugById chưa sẵn sàng
    }

    public LiveData<List<ScheduleWithDrug>> getUpcomingReminders() {
        return upcomingReminders;
    }

    // Gọi hàm này từ Activity/Fragment hiển thị màn hình báo thức
    // public void loadReminderDetail(long reminderId) {
    //    currentReminderId.setValue(reminderId);
    // }
    //
    // public LiveData<ReminderInstanceWithDrug> getCurrentReminderDetail() {
    //    return currentReminderDetail;
    // }

    // Hàm này có thể được gọi từ NotificationActionReceiver hoặc Activity màn hình báo thức
    public void markAsTaken(long reminderId) {
        // Lấy ReminderInstanceEntity, cập nhật trạng thái và lưu lại
        // Chạy trên background thread
        new Thread(() -> {
            ScheduleEntity reminder = repository.getReminderInstanceByIdSync(reminderId);
            if (reminder != null) {
                reminder.status = ScheduleEntity.STATUS_TAKEN;
                reminder.actualTakenTime = System.currentTimeMillis();
                repository.updateReminderInstance(reminder);
                // Không cần đặt lại báo thức cho lần này
                // Hủy báo thức snooze nếu có cho reminder này (AlarmScheduler sẽ xử lý)
                alarmScheduler.cancelSnooze(reminderId);
                Log.d("ReminderViewModel", "Reminder " + reminderId + " marked as TAKEN.");
            }
        }).start();
    }

    public void snoozeReminder(long reminderId, int snoozeMinutes) {
        new Thread(() -> {
            ScheduleEntity reminder = repository.getReminderInstanceByIdSync(reminderId);
            if (reminder != null) {
                reminder.status = ScheduleEntity.STATUS_SNOOZED;
                reminder.snoozeCount += 1;
                // Thời gian nhắc nhở mới sẽ được tính bởi AlarmScheduler
                repository.updateReminderInstance(reminder);

                // Đặt lại báo thức sau N phút
                alarmScheduler.snoozeReminder(reminderId, snoozeMinutes);
                Log.d("ReminderViewModel", "Reminder " + reminderId + " SNOOZED for " + snoozeMinutes + " minutes.");
            }
        }).start();
    }

    public void skipReminder(long reminderId) {
        new Thread(() -> {
            ScheduleEntity reminder = repository.getReminderInstanceByIdSync(reminderId);
            if (reminder != null) {
                reminder.status = ScheduleEntity.STATUS_SKIPPED;
                reminder.actualTakenTime = System.currentTimeMillis(); // Ghi nhận thời điểm bỏ qua
                repository.updateReminderInstance(reminder);
                // Hủy báo thức snooze nếu có cho reminder này (AlarmScheduler sẽ xử lý)
                alarmScheduler.cancelSnooze(reminderId);
                Log.d("ReminderViewModel", "Reminder " + reminderId + " SKIPPED.");
            }
        }).start();
    }

    // Có thể cần một hàm để refresh danh sách upcoming reminders
    public void refreshUpcomingReminders() {
        // Cách đơn giản là re-query, hoặc dùng cơ chế khác phức tạp hơn
        upcomingReminders = repository.getUpcomingReminders(System.currentTimeMillis());
        // Thông báo cho observer nếu cần (LiveData tự làm điều này)
    }

    // Dọn dẹp các reminder cũ đã xử lý
    public void cleanupOldReminders() {
        repository.cleanupOldReminders();
    }
}