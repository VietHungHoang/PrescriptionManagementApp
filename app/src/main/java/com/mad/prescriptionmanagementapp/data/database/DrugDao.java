package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;

import java.util.List;

@Dao
public interface DrugDao {
    @Query("SELECT * FROM drug_cache ORDER BY name ASC")
    LiveData<List<DrugCache>> getAllDrugsFromCache(); // Trả về LiveData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DrugCache> drugs);

    @Query("DELETE FROM drug_cache")
    void deleteAll();

    // (Tùy chọn) Đếm số lượng bản ghi trong cache
    @Query("SELECT COUNT(*) FROM drug_cache")
    int getDrugCount();

    @Query("SELECT COUNT(*) FROM drug_cache")
    LiveData<Integer> getDrugCount1();
}