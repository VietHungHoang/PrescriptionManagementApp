package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.DrugDao;
import com.mad.prescriptionmanagementapp.data.database.PrescriptionDao;
import com.mad.prescriptionmanagementapp.data.database.UnitDao;
import com.mad.prescriptionmanagementapp.data.mapper.DrugInPresMapper;
import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.mapper.PrescriptionMapper;
import com.mad.prescriptionmanagementapp.data.mapper.UnitMapper;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.DrugService;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        this.databaseExecutor.execute(() -> {
            prescriptionDao.insertPrescriptionAndComponent(prescription);
        });
    }
}