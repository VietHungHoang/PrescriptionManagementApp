package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.PrescriptionDao;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.PrescriptionService;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.LoginResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.util.SharedPrefUtils;

import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionRepository {
    private static final String TAG = "PrescriptionRepositoty";
    private final PrescriptionDao prescriptionDao;
    private final PrescriptionService prescriptionService;
    private final ExecutorService databaseExecutor;
    private final boolean isFetchInProgress = false;
    private final boolean isFetchUnit = false;

    private SharedPrefUtils sharedPrefUtils;

    public PrescriptionRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        this.prescriptionDao = database.prescriptionDao();
        this.databaseExecutor = AppDatabase.databaseWriteExecutor;
        this.prescriptionService = NetworkClient.getPrescriptionService();
        sharedPrefUtils = new SharedPrefUtils(application);
    }

    public void insert(Prescription prescription) {
        prescriptionDao.insertPrescriptionAndComponent(prescription);
    }

    public void saveToServer(PrescriptionRequest prescriptionRequest) {
        if(sharedPrefUtils.getToken() != null) {
            String token  = "Bearer " + sharedPrefUtils.getToken();

            Call<ResponseObject<Void>> call = prescriptionService.saveToServer(token, prescriptionRequest);
            call.enqueue(new Callback<ResponseObject<Void>>() {
                @Override
                public void onResponse(Call<ResponseObject<Void>> call, Response<ResponseObject<Void>> response) {
                    Long a = 5L;
                }

                @Override
                public void onFailure(Call< ResponseObject<Void>> call, Throwable t) {
                    int b = 1;
                }
            });
        }
    }
}