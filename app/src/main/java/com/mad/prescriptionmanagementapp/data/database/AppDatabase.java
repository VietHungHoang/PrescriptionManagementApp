package com.mad.prescriptionmanagementapp.data.database;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.model.Unit;

@Database(entities = {DrugCache.class, Unit.class /*, Các entity khác */}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DrugDao drugDao();
    // Khai báo các abstract method cho DAO khác
    public abstract UnitDao unitDao();

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