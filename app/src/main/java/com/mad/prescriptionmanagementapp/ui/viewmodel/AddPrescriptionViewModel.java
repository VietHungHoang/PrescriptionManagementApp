package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.ScheduleDao;
import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.data.repository.DrugRepository;
import com.mad.prescriptionmanagementapp.data.repository.PrescriptionRepository;
import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;

public class AddPrescriptionViewModel extends AndroidViewModel {
    private DrugRepository drugRepository;
    @Getter
//    private final LiveData<List<SimpleDrug>> originalDrugList;
    private LiveData<List<Unit>> originalUnitList;
    private final MutableLiveData<List<DrugInPres>> listSelectedDrug = new MutableLiveData<>();

    private final MutableLiveData<DrugInPres> currentDrug = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<PrescriptionRequest> prescription = new MutableLiveData<>();

    private MutableLiveData<List<TimeDosage>> listTime = new MutableLiveData<>();

    private MutableLiveData<TimeDosage> currentTimeDosage = new MutableLiveData<>();

    private MutableLiveData<String> drugUnit = new MutableLiveData<>();

    private MutableLiveData<Boolean> onMedicalInfo = new MutableLiveData<>();
    private PrescriptionRepository prescriptionRepository;

    private AppDatabase db;
    private ScheduleDao reminderDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void removeSelectedDrug() {
        List<DrugInPres> selectedDrug = this.listSelectedDrug.getValue();
        for(int i = 0; i < selectedDrug.size(); i++) {
            if(selectedDrug.get(i).getDrug().getId() == this.currentDrug.getValue().getDrug().getId()) {
                selectedDrug.remove(i);
                break;
            }
        }
        this.setCurrentDrug(null);
        this.listSelectedDrug.setValue(selectedDrug);
    }

    public LiveData<Boolean> isOnMedicalInfo() {
        return this.onMedicalInfo;
    }

    public void setOnMedicalInfo(boolean isOn) {
        this.onMedicalInfo.setValue(isOn);
    }


    @Getter
    private boolean nameEmpty;

    public void addNote(String note) {
        DrugInPres drug = this.currentDrug.getValue();
        if(drug != null) {
            drug.setNote(note);
            this.currentDrug.setValue(drug);
        }

    }

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

    public LiveData<String> getDrugName() {
        return Transformations.map(this.prescription, pres ->
                pres != null ? pres.getName() : ""
        );
    }

    public void updateSelectedDrugs(String date) {
        DrugInPres currentDrug = this.currentDrug.getValue();
        if (currentDrug != null) {
            currentDrug.setTimeDosages(TimeDosage.deepCopyList(this.listTime.getValue()));
            currentDrug.setStartDate(date);
            List<DrugInPres> selectedDrugs = this.listSelectedDrug.getValue();
            selectedDrugs.add(currentDrug);
            this.listSelectedDrug.setValue(selectedDrugs);
            this.currentDrug.setValue(null);
        }


    }

    public LiveData<List<DrugInPres>> getSelectedDrugs() {
        return this.listSelectedDrug;
    }

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
                drug != null ? drug.getSimpleDrug().getName() : ""
        );
    }

    public void setTimeAndDosage(TimeDosage timeDosage) {
        if (this.listTime.getValue() == null) {
            this.listTime.setValue(new ArrayList<>());
        }
        List<TimeDosage> list = this.listTime.getValue();
        list.add(timeDosage);
        this.listTime.setValue(list);
    }

    public void resetListTimeDosage() {
        this.listTime.setValue(new ArrayList<>());
    }

    public String getDayBetween() {
        DrugInPres drug = this.currentDrug.getValue();
        if (drug != null) {
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
        if (oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.DAILY);
            this.currentDrug.setValue(drug);
        }
    }

    public void setFrequencyEveryNDay(int days) {
        DrugInPres oldDrug = this.currentDrug.getValue();
        if (oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.EVERY_N_DAYS);
            drug.setEveryNDays(days);
            this.currentDrug.setValue(drug);
        }
    }

    public void setFrequencySpecificDay(List<Integer> days) {
        DrugInPres oldDrug = this.currentDrug.getValue();
        if (oldDrug != null) {
            DrugInPres drug = new DrugInPres(oldDrug);
            drug.setFrequency(Frequency.SPECIFIC_DAYS);
            drug.setSpecificDays(days);
            this.currentDrug.setValue(drug);
        }
    }


    public void setCurrentDrug(DrugInPres drug) {
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
            if (this.validateAddPrescriptionInfo(pres.getName())) {
                pres.setDrugs(this.listSelectedDrug.getValue());
                this.setupReminder(context);
            }
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
