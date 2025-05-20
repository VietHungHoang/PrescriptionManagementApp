package com.mad.prescriptionmanagementapp.util;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.ScheduleRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleGenerationHelper {

    // Dùng AtomicInteger để đảm bảo unique request code nếu tạo nhiều reminder cùng lúc
    private static final AtomicInteger alarmRequestCodeCounter = new AtomicInteger((int) System.currentTimeMillis());

//    public static List<ScheduleEntity> generateSchedules(Prescription prescription, LocalDate toDate) {
//        List<ScheduleEntity> schedules = new ArrayList<>();
//        if (prescription == null || prescription.getDrugs() == null) {
//            return schedules;
//        }
//
//        for (DrugInPres drugInPres : prescription.getDrugs()) {
//            if (!Validation.isValidList(drugInPres.getTimeDosages())) {
//                continue;
//            }
//
//            LocalDate startDate = LocalDate.parse(drugInPres.getStartDate()); // Giả sử drugInPres.getDate() là "YYYY-MM-DD"
//
//            for (LocalDate currentDate = startDate; !currentDate.isAfter(toDate); currentDate = currentDate.plusDays(1)) {
//                if (isShouldTakeToday(drugInPres, startDate, currentDate)) {
//                    addTimeSchedule(schedules, drugInPres, currentDate);
//                }
//            }
//        }
//        return schedules;
//    }

    public static List<ScheduleEntity> generateSchedulesForADrug(DrugInPres drugInPres, LocalDate toDate) {
        List<ScheduleEntity> schedules = new ArrayList<>();

        if (!Validation.isValidList(drugInPres.getTimeDosages())) {
            return schedules;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(drugInPres.getStartDate(), formatter); // Giả sử drugInPres.getDate() là "YYYY-MM-DD"

        for (LocalDate currentDate = startDate; !currentDate.isAfter(toDate); currentDate = currentDate.plusDays(1)) {
            if (isShouldTakeToday(drugInPres, startDate, currentDate)) {
                addTimeSchedule(schedules, drugInPres, currentDate);
            }
        }
        return schedules;
    }

    public static List<ScheduleRequest> generateSchedulesForADrugRequest(DrugInPres drugInPres, LocalDate toDate) {
        List<ScheduleRequest> schedules = new ArrayList<>();

        if (!Validation.isValidList(drugInPres.getTimeDosages())) {
            return schedules;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(drugInPres.getStartDate(), formatter); // Giả sử drugInPres.getDate() là "YYYY-MM-DD"

        for (LocalDate currentDate = startDate; !currentDate.isAfter(toDate); currentDate = currentDate.plusDays(1)) {
            if (isShouldTakeToday(drugInPres, startDate, currentDate)) {
                addTimeScheduleRequest(schedules, drugInPres, currentDate);
            }
        }
        return schedules;
    }

    private static boolean isShouldTakeToday(DrugInPres drugInPres, LocalDate startDate, LocalDate currentDate) {
        switch (drugInPres.getFrequency()) {
            case DAILY:
                return true;
            case EVERY_N_DAYS:
                if (drugInPres.getEveryNDays() > 0) {
                    long daysBetween = ChronoUnit.DAYS.between(startDate, currentDate);
                    if (daysBetween % drugInPres.getEveryNDays() == 0) {
                        return true;
                    }
                }
                break;
            case SPECIFIC_DAYS:
                if (drugInPres.getSpecificDays() != null && !drugInPres.getSpecificDays().isEmpty()) {
                    DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
                    // DayOfWeek.getValue() trả về 1 (Thứ Hai) đến 7 (Chủ Nhật)
                    if (drugInPres.getSpecificDays().contains(currentDayOfWeek.getValue())) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private static void addTimeSchedule(List<ScheduleEntity> scheduleEntities, DrugInPres drugInPres, LocalDate currentDate) {
        for (TimeDosage timeDosage : drugInPres.getTimeDosages()) {
            LocalDateTime reminderDateTime = currentDate.atTime(timeDosage.getHour(), timeDosage.getMinutes());
            long scheduledMillisUTC = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // Chỉ tạo reminder cho tương lai
            if (scheduledMillisUTC > System.currentTimeMillis()) {
                Long simpleDrugId = (drugInPres.getSimpleDrug() != null && drugInPres.getSimpleDrug().getId() != null) ? drugInPres.getSimpleDrug().getId() : -1L;
                double dosage = timeDosage.getDosage();
                scheduleEntities.add(new ScheduleEntity(
                        simpleDrugId,
                        null,
                        scheduledMillisUTC,
                        ReminderStatus.PENDING,
                        alarmRequestCodeCounter.getAndIncrement()
                ));
            }
        }
    }

    private static void addTimeScheduleRequest(List<ScheduleRequest> scheduleEntities, DrugInPres drugInPres, LocalDate currentDate) {
        for (TimeDosage timeDosage : drugInPres.getTimeDosages()) {
            LocalDateTime reminderDateTime = currentDate.atTime(timeDosage.getHour(), timeDosage.getMinutes());
            long scheduledMillisUTC = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // Chỉ tạo reminder cho tương lai
            if (scheduledMillisUTC > System.currentTimeMillis()) {
                double dosage = timeDosage.getDosage();
                scheduleEntities.add(new ScheduleRequest(
                        reminderDateTime.toString(),
                        dosage
                ));
            }
        }
    }

    // Ví dụ cách sử dụng:
    // AppDatabase db = AppDatabase.getDatabase(context);
    // List<ScheduledReminderEntity> newReminders = ReminderGenerationHelper.generateReminders(prescription, LocalDate.now(), LocalDate.now().plusDays(30));
    // new Thread(() -> db.scheduledReminderDao().insertAll(newReminders)).start();
}
