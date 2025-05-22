package com.mad.prescriptionmanagementapp.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mad.prescriptionmanagementapp.data.mapper.TimeDosageMapper;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.DosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.util.ScheduleGenerationHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPrescription(PrescriptionEntity prescription);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDrugInPres(DrugInPresEntity drugInPresEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDosage(DosageEntity dosageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertListDrugInPres(List<DrugInPresEntity> drugInPresEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertListTimeDosages(List<DosageEntity> timeDosageEntities);

    @Insert()
    long[] insertListSchedules(List<ScheduleEntity> scheduleEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PrescriptionEntity> prescriptions);

    @Insert()
    long insertUnit(UnitEntity unitEntity);

    @Query("SELECT * FROM prescriptions WHERE local_id = :id")
    PrescriptionEntity getById(Long id);

    @Transaction
    default void insertPrescriptionAndComponent(Prescription prescription) {
        PrescriptionEntity prescriptionEntity = prescription.toEntity();
        long presId = insertPrescription(prescriptionEntity);

        for(DrugInPres drugInPres : prescription.getDrugs()) {
            DrugInPresEntity drugInPresEntity = drugInPres.toEntity();
            drugInPresEntity.setPrescriptionId(presId);
            long drugInPresId = insertDrugInPres(drugInPresEntity);
            for (TimeDosage timeDosage : drugInPres.getTimeDosages()) {
                DosageEntity dosageEntity = timeDosage.toEntity();
                long dosageId = insertDosage(dosageEntity);
                List<ScheduleEntity> scheduleEntities = ScheduleGenerationHelper.generateSchedulesForATime(drugInPres, drugInPresId, timeDosage, dosageId);
                insertListSchedules(scheduleEntities);
            }
        }
    }
};