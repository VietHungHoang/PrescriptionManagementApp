package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.DrugInPresRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.data.repository.DrugRepository;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class AddPrescriptionViewModel extends AndroidViewModel {
    private DrugRepository drugRepository;
    private LiveData<List<DrugResponse>> drugsList;

    private final MutableLiveData<List<DrugInPresRequest>> _selectedDrug = new MutableLiveData<>();

    public LiveData<List<DrugInPresRequest>> selectedDrug = this._selectedDrug;

    public void addDrug(DrugInPresRequest drug) {
        List<DrugInPresRequest> current = this._selectedDrug.getValue();
        if (current == null) current = new ArrayList<>();
        List<DrugInPresRequest> updated = new ArrayList<>(current);
        updated.add(drug);
        this._selectedDrug.setValue(updated);
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


}
