package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
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
    private final MutableLiveData<List<DrugInPres>> listSelectedDrug = new MutableLiveData<>();
    public LiveData<List<DrugInPres>> selectedDrug = this.listSelectedDrug;
    private final MutableLiveData<DrugInPres> currentDrug = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<PrescriptionRequest> prescription = new MutableLiveData<>();

    private MutableLiveData<List<TimeDosage>> listTime = new MutableLiveData<>();

    private MutableLiveData<TimeDosage> currentTimeDosage = new MutableLiveData<>();

    private MutableLiveData<String> drugUnit = new MutableLiveData<>();


    @Getter
    private boolean nameEmpty;

    public void removeTimeDosage(int position) {
        List<TimeDosage> tmp = this.listTime.getValue();
        tmp.remove(position);
        this.listTime.setValue(tmp);
    }

    public LiveData<String> getDrugUnit() {
        return this.drugUnit;
    }

    public void setDrugUnit(String unit) {
        this.drugUnit.setValue(unit);
    }

    public LiveData<List<TimeDosage>> getListTimeDosage() {

        return this.listTime;
    }

    public LiveData<String> getErrorMessage() {
        return this.errorMessage;
    }

    public LiveData<DrugInPres> getCurrentDrug() {
        return this.currentDrug;
    }

    public void setCurrentTimeDosage(TimeDosage timeDosage) {
        this.currentTimeDosage.setValue(timeDosage);
    }

    public TimeDosage getCurrentTimeDosage() {
        return this.currentTimeDosage.getValue();
    }

    public void resetErrorMessage() {
        this.errorMessage.setValue(null);
    }

    public Frequency getFrequency() {
        return this.currentDrug.getValue().getFrequency();
    }

    public LiveData<String> getHospital() {
        return Transformations.map(prescription, pres ->
                pres != null ? pres.getHospital() : ""
        );
    }

    public LiveData<String> getDoctor() {
        return Transformations.map(prescription, pres ->
                pres != null ? pres.getDoctor().getName() : ""
        );
    }

    public LiveData<String> getConsultationDate() {
        return Transformations.map(prescription, pres ->
                pres != null ? pres.getConsultationDate() : ""
        );
    }

    public LiveData<String> getFollowUpDate() {
        return Transformations.map(prescription, pres ->
                pres != null ? pres.getFollowUpDate() : ""
        );
    }

    public LiveData<String> getCurrentDrugName() {
        return Transformations.map(currentDrug, drug ->
                drug != null ? drug.getDrugResponse().getName() : ""
        );
    }

    public void setTimeAndDosage(TimeDosage timeDosage) {
        if(this.listTime.getValue() == null) {
            this.listTime.setValue(new ArrayList<>());
        }
        List<TimeDosage> list = this.listTime.getValue();
        list.add(timeDosage);
        this.listTime.setValue(list);
    }

    public String getDayBetween() {
        DrugInPres drug = this.currentDrug.getValue();
        if(drug != null) {
            return String.valueOf(this.currentDrug.getValue().getEveryNDays());
        }
        return "1";
    }

    public List<Integer> getSpecificDays() {
        DrugInPres drug = this.currentDrug.getValue();
        return drug != null ? drug.getSpecificDays() : new ArrayList<>();
    }

    public void setFrequencyDaily() {
        DrugInPres oldDrug = this.currentDrug.getValue();
        if(oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.DAILY);
            this.currentDrug.setValue(drug);
        }
    }
    public void setFrequencyEveryNDay(int days) {
        DrugInPres oldDrug = this.currentDrug.getValue();
        if(oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.EVERY_N_DAY);
            drug.setEveryNDays(days);
            this.currentDrug.setValue(drug);
        }
    }
    public void setFrequencySpecificDay(List<Integer> days) {
        DrugInPres oldDrug = this.currentDrug.getValue();
        if(oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.SPECIFIC_DAYS);
            drug.setSpecificDays(days);
            this.currentDrug.setValue(drug);
        }
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

    public AddPrescriptionViewModel(@NonNull Application application) {
        super(application);
        this.drugRepository = new DrugRepository(application); // Consider upgrading to use DI

        this.drugsList = Transformations.map(this.drugRepository.getCachedDrugs(), entities -> {
            if (entities == null) return null;
            return entities.stream()
                    .map(DrugMapper::cacheToResponse) // Chuyển Entity -> DTO
                    .collect(Collectors.toList());
        });
        List<DrugResponse> drugs = this.drugsList.getValue();
        this.listTime.setValue(new ArrayList<>());
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
