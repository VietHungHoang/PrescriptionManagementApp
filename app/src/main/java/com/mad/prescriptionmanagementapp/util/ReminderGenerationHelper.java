package com.mad.prescriptionmanagementapp.util;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduledReminderEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReminderGenerationHelper {

    // Dùng AtomicInteger để đảm bảo unique request code nếu tạo nhiều reminder cùng lúc
    private static final AtomicInteger alarmRequestCodeCounter = new AtomicInteger((int) System.currentTimeMillis());

    public static List<ScheduledReminderEntity> generateReminders(Prescription prescription, LocalDate fromDate, LocalDate toDate) {
        List<ScheduledReminderEntity> reminders = new ArrayList<>();
        if (prescription == null || prescription.getDrugs() == null) {
            return reminders;
        }

        for (DrugInPres drugInPres : prescription.getDrugs()) {
            if (drugInPres.getTimeDosages() == null || drugInPres.getTimeDosages().isEmpty()) {
                continue;
            }

            LocalDate startDate = LocalDate.parse(drugInPres.getDate()); // Giả sử drugInPres.getDate() là "YYYY-MM-DD"

            for (LocalDate currentDate = fromDate; !currentDate.isAfter(toDate); currentDate = currentDate.plusDays(1)) {
                if (currentDate.isBefore(startDate)) {
                    continue; // Chưa đến ngày bắt đầu uống thuốc này
                }

                boolean shouldTakeToday = false;
                switch (drugInPres.getFrequency()) {
                    case DAILY:
                        shouldTakeToday = true;
                        break;
                    case EVERY_N_DAY:
                        if (drugInPres.getEveryNDays() > 0) {
                            long daysBetween = ChronoUnit.DAYS.between(startDate, currentDate);
                            if (daysBetween % drugInPres.getEveryNDays() == 0) {
                                shouldTakeToday = true;
                            }
                        }
                        break;
                    case SPECIFIC_DAYS: // Giả sử specificDays là List<Integer> với 1=MONDAY, ..., 7=SUNDAY
                        if (drugInPres.getSpecificDays() != null && !drugInPres.getSpecificDays().isEmpty()) {
                            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek(); // java.time.DayOfWeek
                            // DayOfWeek.getValue() trả về 1 (Thứ Hai) đến 7 (Chủ Nhật)
                            if (drugInPres.getSpecificDays().contains(currentDayOfWeek.getValue())) {
                                shouldTakeToday = true;
                            }
                        }
                        break;
                    // Thêm các case khác nếu có
                }

                if (shouldTakeToday) {
                    for (TimeDosage timeDosage : drugInPres.getTimeDosages()) {
                        LocalDateTime reminderDateTime = currentDate.atTime(timeDosage.getHour(), timeDosage.getMinutes());
                        long scheduledMillisUTC = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        // Hoặc nếu backend trả giờ UTC:
                        // long scheduledMillisUTC = reminderDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

                        // Chỉ tạo reminder cho tương lai
                        if (scheduledMillisUTC > System.currentTimeMillis()) {
                            reminders.add(new ScheduledReminderEntity(
                                    prescription.getId(),
                                    // Cần có ID cho DrugInPres, hoặc bạn có thể dùng index/hashcode tạm
                                    // Nếu DrugInPres không có ID riêng, bạn cần cơ chế để xác định nó
                                    // Giả sử DrugInPres có một ID (ví dụ drugInPres.getLocalId() hoặc tạo 1 ID khi parse)
                                    (drugInPres.getDrugResponse() != null && drugInPres.getDrugResponse().getId() != null) ? drugInPres.getDrugResponse().getId() : -1L, // ID của thuốc
                                    drugInPres.getDrugResponse() != null ? drugInPres.getDrugResponse().getName() : "N/A",
                                    timeDosage.getDosage(),
                                    drugInPres.getUnit() != null ? drugInPres.getUnit().getName() : "N/A",
                                    drugInPres.getDrugResponse() != null ? drugInPres.getDrugResponse().getName() : null,
                                    scheduledMillisUTC,
                                    ReminderStatus.PENDING,
                                    alarmRequestCodeCounter.getAndIncrement() // Tạo request code duy nhất
                            ));
                        }
                    }
                }
            }
        }
        return reminders;
    }

    // Ví dụ cách sử dụng:
    // AppDatabase db = AppDatabase.getDatabase(context);
    // List<ScheduledReminderEntity> newReminders = ReminderGenerationHelper.generateReminders(prescription, LocalDate.now(), LocalDate.now().plusDays(30));
    // new Thread(() -> db.scheduledReminderDao().insertAll(newReminders)).start();
}
