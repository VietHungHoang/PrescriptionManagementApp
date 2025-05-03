package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.data.repository.DrugRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class AddPrescriptionViewModel extends AndroidViewModel {
    private DrugRepository drugRepository;
    private LiveData<List<DrugResponse>> drugsList;
    private final MutableLiveData<List<DrugInPres>> listSelectedDrug = new MutableLiveData<>();
    public LiveData<List<DrugInPres>> selectedDrug = this.listSelectedDrug;
    private MutableLiveData<DrugInPres> currentDrug = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    @Getter
    private boolean nameEmpty;

    public LiveData<String> getErrorMessage() {
        return this.errorMessage;
    }

    public void resetErrorMessage() {
        this.errorMessage.setValue(null);
    }

    public LiveData<String> getCurrentDrugName() {
        return Transformations.map(currentDrug, drug ->
                drug != null ? drug.getDrugResponse().getName() : ""
        );
    }


    public void addCurrentDrug(DrugInPres drug) {
        this.currentDrug.setValue(drug);
    }
    public void addDrug(DrugInPres drug) {
        List<DrugInPres> current = this.listSelectedDrug.getValue();
        if (current == null) current = new ArrayList<>();
        List<DrugInPres> updated = new ArrayList<>(current);
        updated.add(drug);
        this.listSelectedDrug.setValue(updated);
    }

    private final MutableLiveData<String> selectedValue = new MutableLiveData<>();

    @Getter
    @Setter
    private Integer frequencyId;

    @Getter
    @Setter
    private PrescriptionRequest prescription;

    public AddPrescriptionViewModel(@NonNull Application application) {
        super(application);
        this.drugRepository = new DrugRepository(application); // Consider upgrading to use DI

        this.drugsList = Transformations.map(this.drugRepository.getCachedDrugs(), entities -> {
            if (entities == null) return null;
            return entities.stream()
                    .map(DrugMapper::cacheToResponse) // Chuyển Entity -> DTO
                    .collect(Collectors.toList());
        });

        this.prescription = new PrescriptionRequest();
    }

    public LiveData<List<DrugResponse>> getDrugsList() {
        return drugsList;
    }

    // Hàm để lấy danh sách thuốc
//    public void getDrugsName() {
//        this.drugRepository.getAllDrugsSimple(new Callback<ResponseObject<List<DrugNameResponse>>>() {
//            @Override
//            public void onResponse(Call<ResponseObject<List<DrugNameResponse>>> call, Response<ResponseObject<List<DrugNameResponse>>> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null && response.body().getData() != null) {
//                        drugsList.postValue(response.body().getData());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseObject<List<DrugNameResponse>>> call, Throwable throwable) {
//                System.out.println(throwable.getMessage());
//            }
//        });
//    }

    // Login handle for error message
    private boolean validateAddPrescriptionInfo(String presName) {
        if(presName.trim().isEmpty()) {
            this.nameEmpty = true;
            this.errorMessage.setValue("Tên đơn thuốc không được để trống");
            return false;

        } else {
            this.nameEmpty = false;
            if(this.selectedDrug.getValue() == null || this.selectedDrug.getValue().isEmpty()) {
                this.errorMessage.setValue("Vui lòng nhập ít nhất một thuốc");
                return false;
            }
        }
        return true;
    }

    public void handleBtnSavePres(String presName) {
        if(this.validateAddPrescriptionInfo(presName)) {

        }
    }



}
