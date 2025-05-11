package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mad.prescriptionmanagementapp.data.mapper.DrugInPresMapper;
import com.mad.prescriptionmanagementapp.data.mapper.PrescriptionMapper;
import com.mad.prescriptionmanagementapp.data.mapper.TimeDosageMapper;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.util.ScheduleGenerationHelper;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Dao
public interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPrescription(PrescriptionEntity prescription);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDrugInPres(DrugInPresEntity drugInPresEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTimeDosage(TimeDosageEntity timeDosageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertListDrugInPres(List<DrugInPresEntity> drugInPresEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertListTimeDosages(List<TimeDosageEntity> timeDosageEntities);

    @Insert()
    long[] insertListSchedules(List<ScheduleEntity> scheduleEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PrescriptionEntity> prescriptions);

    @Insert()
    long insertUnit(UnitEntity unitEntity);

    @Query("SELECT * FROM prescriptions WHERE local_id = :id")
    PrescriptionEntity getById(Long id);

    @Transaction
    default void insertPrescriptionAndComponent(PrescriptionRequest prescription) {
        PrescriptionEntity prescriptionEntity = new PrescriptionEntity(prescription.getName(), prescription.getHospital(), prescription.getDoctorName(), null, null);
        long presId = insertPrescription(prescriptionEntity);

        for(int i = 0; i < prescription.getDrugs().size(); i++) {
            DrugInPres drugInPres = prescription.getDrugs().get(i);
            DrugInPresEntity drugInPresEntity = DrugInPresEntity.modelToEntity(drugInPres);
            drugInPresEntity.setPrescriptionId(presId);
            long drugInPresId = insertDrugInPres(drugInPresEntity);
            List<TimeDosageEntity> timeDosageEntities = new ArrayList<>();
            for (TimeDosage timeDosage : drugInPres.getTimeDosages()) {
                TimeDosageEntity entity = TimeDosageMapper.modelToEntity(timeDosage);
                entity.setDrugInPresId(drugInPresId);
                timeDosageEntities.add(entity);
            };
            long timeDosageId = insertTimeDosage(timeDosageEntities.get(0));

            List<ScheduleEntity> scheduleEntities = ScheduleGenerationHelper.generateSchedulesForADrug(drugInPres, LocalDate.now().plusDays(10));
            for(ScheduleEntity x : scheduleEntities) {
                x.setTimeDosageId(timeDosageId);
                x.setDrugInPresId(drugInPresId);
            }
            insertListSchedules(scheduleEntities);
        }

    }

//    @Query("SELECT * FROM prescriptions WHERE local_id = :id")
//    LiveData<PrescriptionEntity> getPrescriptionById(Long id);
//
//    @Query("SELECT * FROM prescriptions ORDER BY consultation_date DESC")
//    LiveData<List<PrescriptionEntity>> getAllPrescriptions();
//
//    // Lấy đơn thuốc và tất cả các chi tiết thuốc trong đơn đó
////    @Transaction
////    @Query("SELECT * FROM prescriptions WHERE id = :prescriptionId")
////    LiveData<PrescriptionWithDrugDetails> getPrescriptionWithDetailsById(Long prescriptionId);
////
////    @Transaction
////    @Query("SELECT * FROM prescriptions ORDER BY consultationDate DESC")
////    LiveData<List<PrescriptionWithDrugDetails>> getAllPrescriptionsWithDetails();
//
//    @Query("DELETE FROM prescriptions WHERE local_id = :prescriptionId")
//    void deletePrescriptionById(long prescriptionId); // Xóa đơn thuốc (sẽ cascade xóa DrugInPres, TimeDosage, ReminderInstance)
};