//package com.mad.prescriptionmanagementapp.data.repository;
//
//import android.util.Log;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Transformations; // Để map dữ liệu từ DB entities
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.TimeZone;
//import java.util.concurrent.Executor; // Inject Executor để chạy tác vụ nền
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//import retrofit2.Response;
//
//@Singleton
//public class ReminderRepositoryImpl implements ReminderRepository {
//
//    private static final String TAG = "ReminderRepositoryImpl";
//
//    private final ScheduleDao scheduleDao;
//    private final MedicationDao medicationDao;
//    private final MedicationApiService apiService;
//    private final SimpleDateFormat isoFormatter;
//    private final Executor ioExecutor; // Executor cho DB và Network IO
//
//    @Inject
//    public ReminderRepositoryImpl(ScheduleDao scheduleDao, MedicationDao medicationDao, MedicationApiService apiService, Executor ioExecutor) {
//        this.scheduleDao = scheduleDao;
//        this.medicationDao = medicationDao;
//        this.apiService = apiService;
//        this.ioExecutor = ioExecutor; // Nhận Executor từ DI
//
//        // Định dạng ISO 8601 UTC
//        this.isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
//        this.isoFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//    }
//
//    // --- Triển khai các phương thức ---
//
//    // Lấy dữ liệu Blocking (chỉ gọi từ background thread)
//    @Override
//    public MedicationReminderDetails getReminderDetailsBlocking(long scheduleId) {
//        try {
//            ScheduleEntity scheduleEntity = scheduleDao.getScheduleByIdBlocking(scheduleId); // Giả sử DAO có hàm blocking
//            if (scheduleEntity == null) return null;
//
//            MedicationEntity medicationEntity = null;
//            if (scheduleEntity.getMedicationId() != null) {
//                medicationEntity = medicationDao.getMedicationByIdBlocking(scheduleEntity.getMedicationId()); // Giả sử DAO có hàm blocking
//            }
//
//            return mapToReminderDetails(scheduleEntity, medicationEntity);
//        } catch (Exception e) {
//            Log.e(TAG, "Error getting reminder details blocking", e);
//            return null;
//        }
//    }
//
//    // Lấy dữ liệu qua LiveData (an toàn gọi từ main thread)
//    @Override
//    public LiveData<MedicationReminderDetails> getReminderDetailsLiveData(long scheduleId) {
//        // Lấy LiveData<ScheduleEntity> từ DAO
//        LiveData<ScheduleEntity> scheduleLiveData = scheduleDao.getScheduleByIdLiveData(scheduleId);
//
//        // Sử dụng Transformations.switchMap để lấy MedicationEntity khi ScheduleEntity thay đổi
//        return Transformations.switchMap(scheduleLiveData, schedule -> {
//            if (schedule == null || schedule.getMedicationId() == null) {
//                // Trả về LiveData chỉ chứa Schedule nếu không có Medication ID
//                MutableLiveData<MedicationReminderDetails> details = new MutableLiveData<>();
//                if (schedule != null) {
//                    details.postValue(mapToReminderDetails(schedule, null));
//                } else {
//                    details.postValue(null);
//                }
//                return details;
//            }
//            // Lấy LiveData<MedicationEntity>
//            LiveData<MedicationEntity> medicationLiveData = medicationDao.getMedicationByIdLiveData(schedule.getMedicationId());
//            // Kết hợp cả hai LiveData
//            return Transformations.map(medicationLiveData, medication -> mapToReminderDetails(schedule, medication));
//        });
//    }
//
//
//    // Gửi log (Blocking - chỉ gọi từ background thread)
//    @Override
//    public Result<ReminderLogResponse> syncReminderLogBlocking(long scheduleId, Long medicationId, long scheduledTimeMillis, long actionTimeMillis, String status) {
//        try {
//            ReminderLogRequest request = new ReminderLogRequest(
//                    scheduleId,
//                    medicationId,
//                    isoFormatter.format(new Date(scheduledTimeMillis)),
//                    isoFormatter.format(new Date(actionTimeMillis)),
//                    status
//            );
//
//            // Thực hiện lệnh gọi API đồng bộ (phải nằm trong background thread)
//            Response<ReminderLogResponse> response = apiService.logReminderAction(request).execute();
//
//            if (response.isSuccessful() && response.body() != null) {
//                return new Result.Success<>(response.body());
//            } else {
//                String errorMsg = "API Error: " + response.code();
//                if (response.errorBody() != null) {
//                    try {
//                        errorMsg += " - " + response.errorBody().string();
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading error body", e);
//                    }
//                }
//                Log.e(TAG, "API call failed: " + errorMsg);
//                return new Result.Error<>(new Exception(errorMsg));
//            }
//        } catch (IOException e) {
//            // Lỗi mạng hoặc IO
//            Log.e(TAG, "Network error syncing reminder log", e);
//            return new Result.Error<>(e);
//        } catch (Exception e) {
//            // Lỗi không xác định khác
//            Log.e(TAG, "Unexpected error syncing reminder log", e);
//            return new Result.Error<>(e);
//        }
//    }
//
//    // --- Hàm tiện ích ---
//    private MedicationReminderDetails mapToReminderDetails(ScheduleEntity schedule, MedicationEntity medication) {
//        if (schedule == null) return null;
//        return new MedicationReminderDetails(
//                schedule.getId(),
//                schedule.getMedicationId(),
//                medication != null ? medication.getName() : "Không rõ thuốc",
//                schedule.getDosageInfo() != null ? schedule.getDosageInfo() : "",
//                medication != null ? medication.getInstructions() : (schedule.getInstructions() != null ? schedule.getInstructions() : null),
//                schedule.getScheduledTimeMillis() // Cần có trường này trong ScheduleEntity
//        );
//    }
//
//    // TODO: Bổ sung các phương thức DAO (getScheduleByIdBlocking, getMedicationByIdBlocking,
//    //       getScheduleByIdLiveData, getMedicationByIdLiveData) trong interface DAO và Room implementation.
//    // TODO: Bổ sung các trường cần thiết (medicationId, dosageInfo, instructions, scheduledTimeMillis)
//    //       vào ScheduleEntity và MedicationEntity.
//}