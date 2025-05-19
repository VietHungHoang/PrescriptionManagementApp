package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.repository.PrescriptionRepository;
import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

public class EditPrescriptionViewModel extends AndroidViewModel {
    @Getter
    private LiveData<List<Unit>> originalUnitList;
    private final MutableLiveData<List<DrugInPres>> listSelectedDrug = new MutableLiveData<>();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<PrescriptionRequest> prescription = new MutableLiveData<>();

    private MutableLiveData<List<TimeDosage>> listTime = new MutableLiveData<>();

    private MutableLiveData<Boolean> onMedicalInfo = new MutableLiveData<>();
    private PrescriptionRepository prescriptionRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void removeCurrentDrug(DrugInPres drugInPres) {
        List<DrugInPres> selectedDrug = this.listSelectedDrug.getValue();
        for(int i = 0; i < selectedDrug.size(); i++) {
            if(selectedDrug.get(i).equals(drugInPres)) {
                selectedDrug.remove(i);
                break;
            }
        }
        this.listSelectedDrug.setValue(selectedDrug);
    }

    @Getter
    private boolean nameEmpty;


    public LiveData<PrescriptionRequest> getPrescription() {
        return this.prescription;
    }

    public void addPresName(String name) {
        PrescriptionRequest pres = this.prescription.getValue();
        pres.setName(name);
        this.prescription.setValue(pres);
    }

    public void addHospital(String name) {
        PrescriptionRequest pres = this.prescription.getValue();
        pres.setHospital(name);
        this.prescription.setValue(pres);
    }

    public void addDoctor(String name) {
        PrescriptionRequest pres = this.prescription.getValue();
        pres.setDoctorName(name);
        this.prescription.setValue(pres);
    }


    public void addConsultionDate(String name) {
        PrescriptionRequest pres = this.prescription.getValue();
        pres.setConsultationDate(name);
        this.prescription.setValue(pres);
    }

    public void addFollowUpDate(String name) {
        PrescriptionRequest pres = this.prescription.getValue();
        pres.setFollowUpDate(name);
        this.prescription.setValue(pres);
    }


    public boolean existedPres() {
        if (this.prescription != null) {
            PrescriptionRequest pres = this.prescription.getValue();
            if ((pres.getName() != null
                    && pres.getName() != "")
                    || (pres.getDrugs() != null && !pres.getDrugs().isEmpty())
                    || (pres.getDoctorName() != null
                    && pres.getDoctorName() != "")
                    || (pres.getHospital() != null
                    && pres.getHospital() != "")
                    || (pres.getConsultationDate() != null
                    && pres.getConsultationDate() != "")
                    || (pres.getFollowUpDate() != null
                    && pres.getFollowUpDate() != "")
                    || !this.listSelectedDrug.getValue().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean existedDrug(long drugId) {
        if (this.listSelectedDrug != null) {
            for (DrugInPres drug : this.listSelectedDrug.getValue()) {
                if (drug.getDrug().getId() == drugId) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updatePrescription(String presName, boolean isOnMedicationInfo, String hospital, String doctor, String consultationDate, String followUpDate) {
        PrescriptionRequest pres;
        this.onMedicalInfo.setValue(isOnMedicationInfo);
        pres = new PrescriptionRequest(presName, hospital, doctor, consultationDate, followUpDate);
        this.prescription.setValue(pres);
    }

    public void updateSelectedDrug(DrugInPres oldDrug, DrugInPres newDrug) {
        List<DrugInPres> selectedDrugs = this.listSelectedDrug.getValue();
        for(int i = 0; i < selectedDrugs.size(); i++) {
            if(selectedDrugs.get(i).equals(oldDrug)) {
                selectedDrugs.set(i, newDrug);
                return;
            }
        }
        this.listSelectedDrug.setValue(selectedDrugs);
    }

    public void addSelectedDrug(DrugInPres drug) {
            List<DrugInPres> selectedDrugs = this.listSelectedDrug.getValue();
            selectedDrugs.add(drug);
            this.listSelectedDrug.setValue(selectedDrugs);
    }

    public LiveData<List<DrugInPres>> getSelectedDrugs() {
        return this.listSelectedDrug;
    }

    public LiveData<String> getErrorMessage() {
        return this.errorMessage;
    }

    public AddPrescriptionViewModel(@NonNull Application application) {
        super(application);
        this.prescriptionRepository = new PrescriptionRepository(application);
        this.listTime.setValue(new ArrayList<>());
        this.listSelectedDrug.setValue(new ArrayList<>());
        this.onMedicalInfo.setValue(false);
//        this.originalDrugList = Transformations.map(this.drugRepository.getCachedDrugs(), entities -> {
//            if (entities == null) return null;
//            return entities.stream()
//                    .map(DrugMapper::cacheToResponse) // Chuyển Entity -> DTO
//                    .collect(Collectors.toList());
//        });
//        this.originalUnitList = this.drugRepository.getUnits();
        this.prescription.setValue(new PrescriptionRequest());
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
        if (presName.trim().isEmpty()) {
            this.nameEmpty = true;
            this.errorMessage.setValue("Tên đơn thuốc không được để trống");
            return false;

        } else {
            this.nameEmpty = false;
            if (this.listSelectedDrug.getValue() == null || this.listSelectedDrug.getValue().isEmpty()) {
                this.errorMessage.setValue("Vui lòng nhập ít nhất một thuốc");
                return false;
            }
        }
        return true;
    }

    public void handleBtnSavePres(Context context) {
        PrescriptionRequest pres = this.prescription.getValue();
        if (pres != null) {
            pres.setDrugs(this.listSelectedDrug.getValue());
            this.setupReminder(context);
        }
    }

    private void setupReminder(Context context) {
        PrescriptionRequest pres = this.prescription.getValue();
        executor.execute(() -> {
            this.prescriptionRepository.insert(pres);
            AlarmScheduler.scheduleAlarmsForPendingReminders(context);
        });
    }


}
