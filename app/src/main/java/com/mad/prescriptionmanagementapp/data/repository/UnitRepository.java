package com.mad.prescriptionmanagementapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.UnitDao;
import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.mapper.UnitMapper;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.UnitService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitRepository {
    private static final String TAG = "UnitRepository";
    private final UnitDao unitDao;
    private final ExecutorService databaseExecutor;
    private final UnitService unitService;
    private LiveData<List<Unit>> unitList;

    private volatile boolean isFetchInProgress = false;
    private volatile boolean isFetchingUnit = false;

    public UnitRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        this.unitService = NetworkClient.getUnitService();
        this.unitDao = database.unitDao();
        this.unitList =  Transformations.map(unitDao.getAllUnits(), entities -> {
            if (entities == null) return null;
            return entities.stream()
                    .map(UnitMapper::entityToModel)
                    .collect(Collectors.toList());
        });
        this.databaseExecutor = AppDatabase.databaseWriteExecutor; // Lấy Executor từ AppDatabase
//        this.unitList = unitDao.getAllUnits();
    }

    public LiveData<List<Unit>> getAllUnit() {
        databaseExecutor.execute(() -> {
            int count = this.unitDao.getUnitCount();
            Log.d("DEBUG", "Executor is running");
            if (count == 0 && !isFetchingUnit) {
                this.fetchUnitsFromApi();
            }
        });
        return this.unitList;
    }

    private void fetchUnitsFromApi() {
        this.isFetchingUnit = true;
        Call<ResponseObject<List<UnitResponse>>> call = this.unitService.getAllUnit();
        call.enqueue(new Callback<ResponseObject<List<UnitResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<UnitResponse>>> call,
                                   @NonNull Response<ResponseObject<List<UnitResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UnitResponse> unitResponses = response.body().getData();

                    if (unitResponses != null) {
                        databaseExecutor.execute(() -> {
//                            UnitRepository.this.unitList.postValue(unitResponses.stream()
//                                    .map(UnitMapper::responseToModel)
//                                    .collect(Collectors.toList()));

                            UnitRepository.this.unitDao.deleteAll();
                            UnitRepository.this.unitDao.insertAll(unitResponses.stream()
                                    .map(UnitMapper::responseToEntity)
                                    .collect(Collectors.toList()));
                        });
                    } else {
                    }
                } else {
                    String errorMsg = "Lỗi tải danh sách unit: ";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg += response.body().getMessage();
                    } else {
                        errorMsg += response.code() + " " + response.message();
                    }
                    Log.e(TAG, errorMsg);
                }
                UnitRepository.this.isFetchingUnit = false;
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<UnitResponse>>> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                UnitRepository.this.isFetchingUnit = false;
            }
        });
    }
}
