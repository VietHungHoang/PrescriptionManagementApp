//package com.mad.prescriptionmanagementapp.ui.viewmodel;
//
//package com.yourcompany.yourapp.features.reminder;
//
//import android.app.Application; // Cần Application context cho một số việc
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel; // Sử dụng AndroidViewModel nếu cần Context
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.yourcompany.yourapp.R; // Để lấy string resources
//import com.yourcompany.yourapp.data.model.MedicationReminderDetails;
//import com.yourcompany.yourapp.data.model.ReminderLogStatus;
//import com.yourcompany.yourapp.domain.repository.ReminderRepository;
//import com.yourcompany.yourapp.domain.scheduler.AlarmScheduler;
//import com.yourcompany.yourapp.util.Result;
//import com.yourcompany.yourapp.util.SingleLiveEvent;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.Executor;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.lifecycle.HiltViewModel;
//
//@HiltViewModel
//public class ReminderViewModel extends AndroidViewModel { // Hoặc ViewModel nếu không cần context
//
//    private static final String TAG = "ReminderViewModel";
//
//    private final ReminderRepository reminderRepository;
//    private final AlarmScheduler alarmScheduler;
//    private final Executor ioExecutor; // Executor cho tác vụ nền
//    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler để post về main thread
//
//    // --- LiveData cho UI ---
//    private final MutableLiveData<String> _reminderTimeDisplay = new MutableLiveData<>();
//    public final LiveData<String> reminderTimeDisplay = _reminderTimeDisplay;
//
//    private final MutableLiveData<String> _medicationName = new MutableLiveData<>();
//    public final LiveData<String> medicationName = _medicationName;
//
//    private final MutableLiveData<String> _medicationDosage = new MutableLiveData<>();
//    public final LiveData<String> medicationDosage = _medicationDosage;
//
//    private final MutableLiveData<String> _medicationInstructions = new MutableLiveData<>();
//    public final LiveData<String> medicationInstructions = _medicationInstructions;
//
//    private final MutableLiveData<Boolean> _hasInstructions = new MutableLiveData<>(false);
//    public final LiveData<Boolean> hasInstructions = _hasInstructions;
//
//    // LiveData cho trạng thái tải/lỗi (có thể thêm nếu cần)
//    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
//    public final LiveData<Boolean> isLoading = _isLoading;
//
//    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
//    public final LiveData<String> errorMessage = _errorMessage;
//
//    // Sự kiện để báo cho Activity đóng lại
//    private final SingleLiveEvent<Void> _closeScreenEvent = new SingleLiveEvent<>();
//    public final LiveData<Void> closeScreenEvent = _closeScreenEvent;
//
//    // Dữ liệu nội bộ
//    private MedicationReminderDetails currentReminderDetails = null;
//
//    // --- Inject Dependencies ---
//    @Inject
//    public ReminderViewModel(
//            @NonNull Application application, // Cần nếu extends AndroidViewModel
//            ReminderRepository reminderRepository,
//            AlarmScheduler alarmScheduler,
//            Executor ioExecutor) {
//        super(application);
//        this.reminderRepository = reminderRepository;
//        this.alarmScheduler = alarmScheduler;
//        this.ioExecutor = ioExecutor;
//    }
//
//    // --- Public Methods ---
//
//    /**
//     * Tải thông tin chi tiết của nhắc nhở khi màn hình được tạo.
//     * @param scheduleId ID của lịch trình.
//     */
//    public void loadReminderDetails(long scheduleId) {
//        if (scheduleId == -1L) {
//            _errorMessage.postValue("ID lịch trình không hợp lệ.");
//            _closeScreenEvent.call(); // Đóng màn hình nếu ID sai
//            return;
//        }
//        _isLoading.setValue(true);
//        // Chạy tác vụ lấy dữ liệu trên background thread
//        ioExecutor.execute(() -> {
//            try {
//                final MedicationReminderDetails details = reminderRepository.getReminderDetailsBlocking(scheduleId);
//                // Post kết quả về main thread để cập nhật LiveData
//                mainHandler.post(() -> {
//                    if (details != null) {
//                        currentReminderDetails = details;
//                        updateUiWithDetails(details);
//                        _isLoading.setValue(false);
//                    } else {
//                        Log.e(TAG, "Không thể tải chi tiết nhắc nhở cho ID: " + scheduleId);
//                        _errorMessage.setValue("Không tìm thấy thông tin nhắc nhở.");
//                        _isLoading.setValue(false);
//                        _closeScreenEvent.call(); // Đóng màn hình nếu không có dữ liệu
//                    }
//                });
//            } catch (Exception e) {
//                Log.e(TAG, "Lỗi khi tải chi tiết nhắc nhở", e);
//                mainHandler.post(() -> {
//                    _errorMessage.setValue("Lỗi khi tải dữ liệu.");
//                    _isLoading.setValue(false);
//                    _closeScreenEvent.call(); // Đóng màn hình nếu lỗi
//                });
//            }
//        });
//    }
//
//    /**
//     * Xử lý khi người dùng nhấn nút "Đã uống".
//     */
//    public void onTakenClicked() {
//        Log.d(TAG, "onTakenClicked");
//        if (currentReminderDetails == null) return;
//        handleAction(ReminderLogStatus.TAKEN);
//    }
//
//    /**
//     * Xử lý khi người dùng nhấn nút "Bỏ qua".
//     */
//    public void onSkipClicked() {
//        Log.d(TAG, "onSkipClicked");
//        if (currentReminderDetails == null) return;
//        handleAction(ReminderLogStatus.SKIPPED);
//    }
//
//    /**
//     * Xử lý khi người dùng nhấn nút "Báo lại".
//     */
//    public void onSnoozeClicked() {
//        Log.d(TAG, "onSnoozeClicked");
//        if (currentReminderDetails == null) return;
//
//        // Đặt lại báo thức sau 10 phút (ví dụ)
//        alarmScheduler.scheduleSnooze(
//                currentReminderDetails.getScheduleId(),
//                currentReminderDetails.getMedicationId()
//                //, 10 // Có thể truyền thời gian snooze tùy chỉnh
//        );
//
//        // Gửi tín hiệu đóng màn hình hiện tại
//        _closeScreenEvent.call();
//    }
//
//    // --- Private Helper Methods ---
//
//    /**
//     * Cập nhật các LiveData cho UI từ dữ liệu chi tiết.
//     * @param details Thông tin chi tiết nhắc nhở.
//     */
//    private void updateUiWithDetails(MedicationReminderDetails details) {
//        // Định dạng thời gian
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        _reminderTimeDisplay.setValue(timeFormat.format(new Date(details.getScheduledTimeMillis())));
//
//        _medicationName.setValue(details.getMedicationName());
//        _medicationDosage.setValue(getApplication().getString(R.string.reminder_dosage_prefix, details.getDosage())); // Ví dụ: "Số lượng: 1 viên"
//
//        if (!TextUtils.isEmpty(details.getInstructions())) {
//            _medicationInstructions.setValue(details.getInstructions());
//            _hasInstructions.setValue(true);
//        } else {
//            _medicationInstructions.setValue(null);
//            _hasInstructions.setValue(false);
//        }
//    }
//
//    /**
//     * Xử lý chung cho hành động "Đã uống" và "Bỏ qua".
//     * @param status Trạng thái ("TAKEN" hoặc "SKIPPED").
//     */
//    private void handleAction(String status) {
//        if (currentReminderDetails == null) return;
//
//        long scheduleId = currentReminderDetails.getScheduleId();
//        Long medicationId = currentReminderDetails.getMedicationId();
//        long scheduledTimeMillis = currentReminderDetails.getScheduledTimeMillis();
//        long actionTimeMillis = System.currentTimeMillis();
//
//        // 1. Hủy bỏ báo thức hiện tại hoặc snooze (nếu có)
//        alarmScheduler.cancelAlarm(scheduleId);
//
//        // 2. Gửi log lên backend (bất đồng bộ)
//        ioExecutor.execute(() -> {
//            Result<ReminderLogResponse> result = reminderRepository.syncReminderLogBlocking(
//                    scheduleId,
//                    medicationId,
//                    scheduledTimeMillis,
//                    actionTimeMillis,
//                    status
//            );
//
//            // Xử lý kết quả trên main thread (nếu cần, ví dụ: hiển thị lỗi)
//            mainHandler.post(() -> {
//                if (result instanceof Result.Success) {
//                    Log.i(TAG, "Hành động '" + status + "' đã được ghi nhận thành công cho schedule " + scheduleId);
//                    // Có thể làm gì đó với response nếu cần
//                } else if (result instanceof Result.Error) {
//                    Exception e = ((Result.Error<ReminderLogResponse>) result).exception;
//                    Log.e(TAG, "Lỗi khi ghi nhận hành động '" + status + "' cho schedule " + scheduleId, e);
//                    // Có thể hiển thị thông báo lỗi cho người dùng
//                    // _errorMessage.setValue("Lỗi đồng bộ: " + e.getMessage());
//                }
//            });
//        });
//
//        // 3. Tính toán và đặt lịch cho lần nhắc nhở TIẾP THEO (nếu cần)
//        // Logic này có thể phức tạp và nên được đặt trong AlarmScheduler hoặc một service riêng
//        // Ví dụ đơn giản: Nếu là lịch hàng ngày, đặt cho ngày mai cùng giờ
//        // ioExecutor.execute(() -> {
//        //     long nextTriggerTime = calculateNextTriggerTimeForSchedule(scheduleId, scheduledTimeMillis);
//        //     if (nextTriggerTime > 0) {
//        //         alarmScheduler.scheduleReminder(scheduleId, medicationId, nextTriggerTime);
//        //     }
//        // });
//
//        // 4. Gửi tín hiệu đóng màn hình
//        _closeScreenEvent.call();
//    }
//
//    // Hàm tính toán lần nhắc tiếp theo (ví dụ)
//    /*
//    private long calculateNextTriggerTimeForSchedule(long scheduleId, long lastScheduledTime) {
//        // TODO: Lấy thông tin chi tiết lịch trình (tần suất, ngày kết thúc...) từ DB
//        // và tính toán thời gian nhắc nhở tiếp theo.
//        // Ví dụ: Nếu là hàng ngày
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(lastScheduledTime);
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
//        // Cần kiểm tra ngày kết thúc lịch trình...
//        return calendar.getTimeInMillis();
//    }
//    */
//
//}