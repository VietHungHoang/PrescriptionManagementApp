package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.PrescriptionDao;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;

import java.util.concurrent.ExecutorService;

public class PrescriptionRepository {
    private static final String TAG = "PrescriptionRepositoty";
    private final PrescriptionDao prescriptionDao;
    private final ExecutorService databaseExecutor;
    private final boolean isFetchInProgress = false;
    private final boolean isFetchUnit = false;

    public PrescriptionRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        this.prescriptionDao = database.prescriptionDao();
        this.databaseExecutor = AppDatabase.databaseWriteExecutor;
    }

    public void insert(PrescriptionRequest prescription) {
        prescriptionDao.insertPrescriptionAndComponent(prescription);
    }
}