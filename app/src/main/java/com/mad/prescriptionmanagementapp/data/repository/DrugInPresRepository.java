package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;
import android.util.Log;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.DrugInPresDao;
import com.mad.prescriptionmanagementapp.data.database.PrescriptionDao;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;

import java.util.concurrent.ExecutorService;

public class DrugInPresRepository {
    private static final String TAG = "DrugInPresRepository";
    private final DrugInPresDao drugInPresDao;
    private final ExecutorService databaseExecutor;
    private final boolean isFetchInProgress = false;
    private final boolean isFetchUnit = false;
    public DrugInPresRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        this.drugInPresDao = database.drugInPresDao();
        this.databaseExecutor = AppDatabase.databaseWriteExecutor;
    }
}