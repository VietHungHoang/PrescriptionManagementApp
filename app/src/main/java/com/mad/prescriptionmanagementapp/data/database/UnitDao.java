package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mad.prescriptionmanagementapp.data.model.Unit;

import java.util.List;

@Dao
public interface UnitDao {
    @Query("SELECT * FROM units ORDER BY name ASC")
    LiveData<List<Unit>> getAllUnits(); // Returns LiveData for reactive updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Unit> units);

    @Query("DELETE FROM units")
    void deleteAll();

    // (Optional) Count records in the unit table
    @Query("SELECT COUNT(*) FROM units")
    int getUnitCount();

    @Query("SELECT COUNT(*) FROM units")
    LiveData<Integer> getUnitCountLive(); // LiveData version of count
}