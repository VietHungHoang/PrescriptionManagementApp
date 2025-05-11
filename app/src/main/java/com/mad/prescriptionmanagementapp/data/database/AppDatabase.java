package com.mad.prescriptionmanagementapp.data.database;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DrugCache.class, UnitEntity.class, DrugEntity.class, PrescriptionEntity.class,
        DrugInPresEntity.class, TimeDosageEntity.class, ScheduleEntity.class/*, Các entity khác */}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UnitDao unitDao();
    public abstract DrugDao drugDao();
    public abstract PrescriptionDao prescriptionDao();
    public abstract ScheduleDao scheduleDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4; // Số luồng cho DB executor


    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "medication_reminder_db")
                             .fallbackToDestructiveMigration() // Chỉ dùng khi dev
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}