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
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduledReminderEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;

@Database(entities = {DrugCache.class, UnitEntity.class, DrugEntity.class, PrescriptionEntity.class,
        DrugInPresEntity.class, TimeDosageEntity.class, ScheduleEntity.class, ScheduledReminderEntity.class/*, Các entity khác */}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UnitDao unitDao();
    public abstract DrugDao drugDao();
    public abstract PrescriptionDao prescriptionDao();
    public abstract DrugInPresDao drugInPresDao();
    public abstract TimeDosageDao timeDosageDao();
    public abstract ScheduleDao scheduleDao();

    public abstract ScheduledReminderDao scheduledReminderDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4; // Số luồng cho DB executor
    public static final java.util.concurrent.ExecutorService databaseWriteExecutor =
            java.util.concurrent.Executors.newFixedThreadPool(NUMBER_OF_THREADS);


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