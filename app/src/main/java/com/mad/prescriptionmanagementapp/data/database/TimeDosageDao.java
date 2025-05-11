package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;

import java.util.List;

@Dao
public interface TimeDosageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Giả sử không có ID duy nhất từ API cho TimeDosage
    void insertAll(List<TimeDosageEntity> timeDosages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TimeDosageEntity timeDosage);

//    @Query("SELECT * FROM time_dosages WHERE drugInPresLocalId = :drugInPresLocalId ORDER BY hour, minutes")
//    LiveData<List<TimeDosageEntity>> getTimeDosagesForDrugInPres(long drugInPresLocalId);
//
//    // Lấy đồng bộ để tạo ReminderInstance
//    @Query("SELECT * FROM time_dosages WHERE drugInPresLocalId = :drugInPresLocalId ORDER BY hour, minutes")
//    List<TimeDosageEntity> getTimeDosagesForDrugInPresSync(long drugInPresLocalId);
//
//    @Query("DELETE FROM time_dosages WHERE drugInPresLocalId = :drugInPresLocalId")
//    void deleteByDrugInPresLocalId(long drugInPresLocalId);
}