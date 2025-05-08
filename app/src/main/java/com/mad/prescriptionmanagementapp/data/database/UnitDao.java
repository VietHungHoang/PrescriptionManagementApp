package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;

import java.util.List;

@Dao
public interface UnitDao {
    @Query("SELECT * FROM units ORDER BY name ASC")
    LiveData<List<UnitEntity>> getAllUnits(); // Returns LiveData for reactive updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UnitEntity> units);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(UnitEntity unit);

    @Query("DELETE FROM units")
    void deleteAll();

    // (Optional) Count records in the unit table
    @Query("SELECT COUNT(*) FROM units")
    int getUnitCount();

    @Query("SELECT COUNT(*) FROM units")
    LiveData<Integer> getUnitCountLive(); // LiveData version of count

    @Query("SELECT * FROM units WHERE id = :id")
    LiveData<UnitEntity> getUnitById(Long id);

    @Query("SELECT * FROM units WHERE id = :id")
    UnitEntity getUnitByIdSync(Long id); // Cho việc lấy tên unit khi tạo ReminderInstance
}

