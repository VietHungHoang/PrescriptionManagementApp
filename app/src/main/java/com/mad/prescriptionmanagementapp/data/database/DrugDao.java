package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;

import java.util.List;

@Dao
public interface DrugDao {
    @Query("SELECT * FROM drugs ORDER BY name ASC")
    LiveData<List<DrugEntity>> getAllDrugsFromCache(); // Trả về LiveData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DrugEntity drug);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DrugEntity> drugs);

    @Query("DELETE FROM drugs")
    void deleteAll();

    // (Tùy chọn) Đếm số lượng bản ghi trong cache
    @Query("SELECT COUNT(*) FROM drugs")
    int getDrugCount();

    @Query("SELECT COUNT(*) FROM drugs")
    LiveData<Integer> getDrugCount1();
}