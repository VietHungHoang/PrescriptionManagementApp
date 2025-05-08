package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

// Import các lớp DTO từ API của bạn (giả sử bạn có chúng)
// import com.yourpackage.data.remote.model.PrescriptionApiDto;
// import com.yourpackage.data.remote.model.DrugInPresApiDto;
// import com.yourpackage.data.remote.model.TimeDosageApiDto;
// import com.yourpackage.data.remote.model.DrugApiDto;
// import com.yourpackage.data.remote.model.UnitApiDto;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.Converters;
import com.mad.prescriptionmanagementapp.data.database.DrugDao;
import com.mad.prescriptionmanagementapp.data.database.DrugInPresDao;
import com.mad.prescriptionmanagementapp.data.database.PrescriptionDao;
import com.mad.prescriptionmanagementapp.data.database.TimeDosageDao;
import com.mad.prescriptionmanagementapp.data.database.UnitDao;
import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;
import com.mad.prescriptionmanagementapp.data.model.relation.PrescriptionWithDrugDetails;
import com.mad.prescriptionmanagementapp.data.model.relation.ScheduleWithDrug;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicationRepository {
    private static final String TAG = "MedicationRepository";

    private final PrescriptionDao prescriptionDao;
    private final DrugInPresDao drugInPresDao;
    private final TimeDosageDao timeDosageDao;
    private final DrugDao drugDao;
    private final UnitDao unitDao;
    private final ScheduleDao scheduleDao;

    private final ExecutorService databaseExecutor;

    public MedicationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        prescriptionDao = db.prescriptionDao();
        drugInPresDao = db.drugInPresDao();
        timeDosageDao = db.timeDosageDao();
        drugDao = db.drugDao();
        unitDao = db.unitDao();
        scheduleDao = db.scheduleDao();

        databaseExecutor = Executors.newSingleThreadExecutor(); // Hoặc một pool lớn hơn nếu cần
    }

    // --- Prescription Operations ---
    public LiveData<List<PrescriptionWithDrugDetails>> getAllPrescriptionsWithDetails() {
        return prescriptionDao.getAllPrescriptionsWithDetails();
    }

    public LiveData<PrescriptionWithDrugDetails> getPrescriptionWithDetailsById(Long prescriptionId) {
        return prescriptionDao.getPrescriptionWithDetailsById(prescriptionId);
    }

    // --- ReminderInstance Operations ---
    public LiveData<List<ScheduleWithDrug>> getUpcomingReminders(long fromTimeMillis) {
        return scheduleDao.getUpcomingRemindersWithDrug(fromTimeMillis,
                ScheduleEntity.STATUS_PENDING, ScheduleEntity.STATUS_SNOOZED);
    }

    public ScheduleEntity getReminderInstanceByIdSync(long reminderId) {
        // Cần chạy trên background thread nếu không phải từ LiveData
        // Trong trường hợp này, Receiver sẽ gọi nó, nên có thể cần đồng bộ.
        // Tuy nhiên, tốt hơn là Receiver chỉ nhận ID và một Service/ViewModel xử lý.
        // Tạm thời để đây cho đơn giản.
        return scheduleDao.getScheduleByIdSync(reminderId);
    }

    public void updateReminderInstance(ScheduleEntity reminderInstance) {
        databaseExecutor.execute(() -> {
            scheduleDao.update(reminderInstance);
            Log.d(TAG, "Updated reminder instance: " + reminderInstance.id + " to status " + reminderInstance.status);
            // Nếu trạng thái là TAKEN hoặc SKIPPED, có thể không cần đặt lại báo thức
            // Nếu SNOOZED, cần đặt lại báo thức mới (sẽ xử lý ở AlarmScheduler)
        });
    }

    public List<ScheduleEntity> getAllPendingOrSnoozedRemindersSync() {
        // Được dùng bởi BOOT_COMPLETED receiver để đặt lại báo thức
        // Cần cẩn thận khi gọi hàm sync này
        return scheduleDao.getAllPendingOrSnoozedSchedulesSync(
                ScheduleEntity.STATUS_PENDING,
                ScheduleEntity.STATUS_SNOOZED
        );
    }


    // --- Complex Operations: Saving Full Prescription and Generating Reminders ---

    /**
     * Lưu một đơn thuốc hoàn chỉnh từ DTO API (hoặc đối tượng tương tự).
     * Đồng thời tạo ra các ReminderInstanceEntity.
     * Đây là một hàm mẫu, bạn cần điều chỉnh cho phù hợp với DTO của mình.
     */
    public void saveFullPrescriptionAndGenerateReminders(
            Prescription prescriptionApiDto // Sử dụng lớp Prescription model bạn cung cấp
    ) {
        databaseExecutor.execute(() -> {
            // 1. Save PrescriptionEntity
            PrescriptionEntity prescriptionEntity = new PrescriptionEntity(
                    prescriptionApiDto.getId(),
                    prescriptionApiDto.getHospital(),
                    prescriptionApiDto.getDoctorName(), // hoặc prescriptionApiDto.getDoctor().getName() nếu có
                    prescriptionApiDto.getConsultationDate(),
                    prescriptionApiDto.getFollowUpDate()
            );
            long prescriptionDbId = prescriptionDao.insert(prescriptionEntity); // Lấy ID đã lưu
            Log.d(TAG, "Saved PrescriptionEntity with ID: " + prescriptionDbId);

            // 2. Iterate through drugs in prescription
            if (prescriptionApiDto.getDrugs() != null) {
                for (DrugInPres drugInPresApiDto : prescriptionApiDto.getDrugs()) {
                    // 2a. Save DrugEntity (if not exists or needs update)
                    Drug drugApiDto = drugInPresApiDto.getDrug(); // DrugResponse của bạn chứa Drug
                    DrugEntity drugEntity = new DrugEntity(
                            drugApiDto.getId(),
                            drugApiDto.getName(), // drug_name
                            drugApiDto.getTitle(),
                            drugApiDto.getImage(),
                            null // sectionsJson - bạn cần convert List<Section> thành JSON string ở đây
                            // Hoặc nếu Section là Entity riêng thì lưu riêng
                    );
                    drugDao.insert(drugEntity); // Giả sử ID là duy nhất từ API
                    Log.d(TAG, "Saved/Updated DrugEntity with ID: " + drugEntity.id);


                    // 2b. Save UnitEntity (if not exists)
                   Unit unitApiDto = drugInPresApiDto.getUnit();
                    UnitEntity unitEntity = new UnitEntity(unitApiDto.getId(), unitApiDto.getName());
                    unitDao.insert(unitEntity);
                    Log.d(TAG, "Saved/Updated UnitEntity with ID: " + unitEntity.id);


                    // 2c. Save DrugInPresEntity
                    // Cần chuyển đổi Frequency enum của bạn sang Converters.Frequency
                    Converters.Frequency freqEnum = Converters.Frequency.UNKNOWN;
                    if (drugInPresApiDto.getFrequency() != null) {
                        try {
                            // Giả sử Frequency enum trong model của bạn có tên giống với Converters.Frequency
                            freqEnum = Converters.Frequency.valueOf(drugInPresApiDto.getFrequency().name());
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Unknown frequency value: " + drugInPresApiDto.getFrequency().name());
                        }
                    }

                    // Xử lý trường "date" của bạn. Giả sử nó là startDate
                    LocalDate startDate = null;
                    if (drugInPresApiDto.getDate() != null && !drugInPresApiDto.getDate().isEmpty()) {
                        try {
                            // Cần đảm bảo drugInPresApiDto.getDate() là định dạng có thể parse thành LocalDate
                            // Ví dụ: "2023-12-25"
                            startDate = LocalDate.parse(drugInPresApiDto.getDate());
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse startDate: " + drugInPresApiDto.getDate(), e);
                            // Có thể đặt một ngày mặc định hoặc bỏ qua nếu không parse được
                            // startDate = LocalDate.now(); // Hoặc xử lý lỗi khác
                        }
                    }
                    // Nếu startDate là null, logic tạo reminder cần xử lý (ví dụ: bắt đầu từ hôm nay)
                    if (startDate == null) startDate = LocalDate.now();


                    DrugInPresEntity drugInPresEntity = new DrugInPresEntity(
                            prescriptionDbId,
                            drugEntity.id,
                            unitEntity.id,
                            startDate,
                            freqEnum,
                            drugInPresApiDto.getEveryNDays(),
                            drugInPresApiDto.getSpecificDays(), // List<Integer>
                            null // originalApiId: Nếu DrugInPres từ API có ID riêng thì gán vào đây
                    );
                    long drugInPresDbLocalId = drugInPresDao.insert(drugInPresEntity);
                    Log.d(TAG, "Saved DrugInPresEntity with local ID: " + drugInPresDbLocalId);


                    // 2d. Save TimeDosageEntities
                    List<TimeDosageEntity> timeDosageEntities = new ArrayList<>();
                    if (drugInPresApiDto.getTimeDosages() != null) {
                        for (TimeDosage timeDosageApiDto : drugInPresApiDto.getTimeDosages()) {
                            timeDosageEntities.add(new TimeDosageEntity(
                                    drugInPresDbLocalId,
                                    timeDosageApiDto.getHour(),
                                    timeDosageApiDto.getMinutes(),
                                    timeDosageApiDto.getDosage()
                            ));
                        }
                        timeDosageDao.insertAll(timeDosageEntities);
                        Log.d(TAG, "Saved " + timeDosageEntities.size() + " TimeDosageEntities for DrugInPres local ID: " + drugInPresDbLocalId);
                    }

                    // 3. Generate ReminderInstances for this DrugInPres
                    generateAndSaveReminderInstances(drugInPresDbLocalId, drugEntity.id, unitEntity.name);
                }
            }
            Log.i(TAG, "Finished saving prescription and generating reminders.");
            // TODO: Sau khi lưu và tạo xong, cần thông báo cho AlarmScheduler để đặt các báo thức mới.
            // Điều này có thể thực hiện qua một LiveData, callback, hoặc EventBus.
        });
    }

    /**
     * Tạo và lưu các ReminderInstanceEntity cho một DrugInPresEntity cụ thể.
     * @param drugInPresLocalId Local ID của DrugInPresEntity trong DB
     * @param drugId ID của thuốc
     * @param unitName Tên đơn vị để hiển thị
     */
    private void generateAndSaveReminderInstances(long drugInPresLocalId, long drugId, String unitName) {
        DrugInPresEntity drugInPres = drugInPresDao.getDrugInPresByIdSync(drugInPresLocalId);
        List<TimeDosageEntity> timeDosages = timeDosageDao.getTimeDosagesForDrugInPresSync(drugInPresLocalId);

        if (drugInPres == null || timeDosages == null || timeDosages.isEmpty()) {
            Log.w(TAG, "Cannot generate reminders, DrugInPres or TimeDosages not found for localId: " + drugInPresLocalId);
            return;
        }

        // Xóa các reminder instance cũ (chưa xảy ra) của drugInPres này trước khi tạo mới
        // Điều này quan trọng nếu lịch trình được cập nhật
        scheduleDao.deleteFuturePendingSchedulesForDrugInPres(
                drugInPresLocalId,
                System.currentTimeMillis(),
                ScheduleEntity.STATUS_PENDING,
                ScheduleEntity.STATUS_SNOOZED
        );
        Log.d(TAG, "Deleted future pending reminders for drugInPresLocalId: " + drugInPresLocalId);


        List<ScheduleEntity> newReminders = new ArrayList<>();
        LocalDate startDate = drugInPres.startDate != null ? drugInPres.startDate : LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(3); // Giới hạn tạo reminder trong 3 tháng tới, bạn có thể tùy chỉnh
        // Nếu PrescriptionEntity có endDate, thì dùng endDate đó nếu nó sớm hơn
        // PrescriptionEntity prescription = prescriptionDao.getPrescriptionByIdSync(drugInPres.prescriptionId); // Cần hàm Sync
        // if (prescription != null && prescription.followUpDate != null) {
        //    if (prescription.followUpDate.isBefore(endDate)) {
        //        endDate = prescription.followUpDate;
        //    }
        // }


        Log.d(TAG, "Generating reminders for drugInPres: " + drugInPresLocalId +
                ", startDate: " + startDate +
                ", endDate: " + endDate +
                ", frequency: " + drugInPres.frequency);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            boolean shouldAddForThisDate = false;
            switch (drugInPres.frequency) {
                case DAILY:
                    shouldAddForThisDate = true;
                    break;
                case EVERY_N_DAYS:
                    if (drugInPres.everyNDays > 0) {
                        long daysBetween = ChronoUnit.DAYS.between(startDate, currentDate);
                        if (daysBetween % drugInPres.everyNDays == 0) {
                            shouldAddForThisDate = true;
                        }
                    }
                    break;
                case SPECIFIC_DATES: // Giả sử specificDays là các ngày trong tháng (1-31)
                    // Hoặc các ngày trong tuần (Calendar.MONDAY, Calendar.TUESDAY...)
                    // Cần làm rõ ý nghĩa của specificDays từ model của bạn.
                    // Ví dụ dưới đây giả sử là ngày trong tuần (1=MONDAY, ..., 7=SUNDAY theo ISO)
                    if (drugInPres.specificDays != null && !drugInPres.specificDays.isEmpty()) {
                        DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); // MONDAY (1) to SUNDAY (7)
                        if (drugInPres.specificDays.contains(currentDayOfWeek.getValue())) {
                            shouldAddForThisDate = true;
                        }
                        // Nếu specificDays là ngày trong tháng (1-31)
                        // if (drugInPres.specificDays.contains(currentDate.getDayOfMonth())) {
                        //    shouldAddForThisDate = true;
                        // }
                    }
                    break;
                case UNKNOWN:
                default:
                    break; // Không tạo reminder
            }

            if (shouldAddForThisDate) {
                for (TimeDosageEntity td : timeDosages) {
                    LocalDateTime reminderDateTime = LocalDateTime.of(currentDate, LocalTime.of(td.hour, td.minutes));
                    // Chỉ tạo reminder nếu thời gian đó chưa qua (hoặc trong ngày hôm nay)
                    if (!reminderDateTime.isBefore(LocalDateTime.now().minusMinutes(5))) { // Cho phép sai số 5 phút
                        long reminderTimestamp = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                        ScheduleEntity reminder = new ScheduleEntity(
                                drugInPresLocalId,
                                drugId,
                                reminderTimestamp,
                                td.dosage,
                                unitName,
                                ScheduleEntity.STATUS_PENDING
                        );
                        newReminders.add(reminder);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (!newReminders.isEmpty()) {
            scheduleDao.insertAll(newReminders); // Dùng insertAll để hiệu quả hơn
            Log.i(TAG, "Generated and saved " + newReminders.size() + " new reminder instances for drugInPresLocalId: " + drugInPresLocalId);
        } else {
            Log.i(TAG, "No new reminder instances generated for drugInPresLocalId: " + drugInPresLocalId);
        }
    }

    /**
     * Xóa tất cả dữ liệu liên quan đến một đơn thuốc (và tạo lại reminder cho các đơn thuốc khác nếu cần).
     * Hoặc chỉ xóa các reminder của đơn thuốc đó.
     */
    public void deletePrescriptionAndAssociatedReminders(Long prescriptionId) {
        databaseExecutor.execute(() -> {
            // Lấy danh sách các DrugInPres thuộc prescription này
            // LiveData<List<DrugInPresEntity>> drugInPresListLiveData = drugInPresDao.getDrugsInPrescriptionRaw(prescriptionId);
            // Cần có hàm Sync để lấy danh sách này. Ví dụ:
            // List<DrugInPresEntity> drugInPresList = drugInPresDao.getDrugsInPrescriptionRawSync(prescriptionId);
            // if (drugInPresList != null) {
            //    for (DrugInPresEntity dip : drugInPresList) {
            //        // Hủy các AlarmManager intents liên quan đến dip.localId (cần AlarmScheduler)
            //        // alarmScheduler.cancelRemindersFor(dip.localId);
            //        reminderInstanceDao.deleteAllRemindersForDrugInPres(dip.localId);
            //    }
            // }
            // Cuối cùng xóa prescription (sẽ cascade xóa DrugInPres, TimeDosage)
            // Room sẽ tự xử lý cascade delete cho DrugInPres, TimeDosage, và ReminderInstance
            // nếu foreign keys được thiết lập onDelete = ForeignKey.CASCADE
            prescriptionDao.deletePrescriptionById(prescriptionId);
            Log.i(TAG, "Deleted prescription and associated data for ID: " + prescriptionId);
            // TODO: Sau khi xóa, cần thông báo cho AlarmScheduler để hủy các báo thức liên quan.
        });
    }

    /**
     * Xóa các reminder cũ đã được xử lý (TAKEN, SKIPPED)
     */
    public void cleanupOldReminders() {
        databaseExecutor.execute(() -> {
            // Xóa các reminder đã uống/bỏ qua cũ hơn 30 ngày chẳng hạn
            long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
            scheduleDao.deleteOldTakenOrSkippedSchedules(thirtyDaysAgo,
                    ScheduleEntity.STATUS_TAKEN,
                    ScheduleEntity.STATUS_SKIPPED);
            Log.i(TAG, "Cleaned up old reminders.");
        });
    }
}