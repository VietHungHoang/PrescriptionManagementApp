package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.repository.UnitRepository;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class AddScheduleViewModel extends AndroidViewModel {

    private final UnitRepository unitRepository;
    @Getter
    private final LiveData<List<Unit>> unitList;

    private final MutableLiveData<DrugInPres> curDrug;
    private final MutableLiveData<Unit> curUnit;

    private final MutableLiveData<Frequency> frequency;
    private MutableLiveData<List<TimeDosage>> listTime;

    private Integer dayBetween;

    @Getter
    private List<Integer> specificDays;


    @Getter
    @Setter
    private boolean isEdited;

    public AddScheduleViewModel(@NonNull Application application) {
        super(application);
        this.unitRepository = new UnitRepository(application);
        this.unitList = this.unitRepository.getAllUnit();
        this.curDrug = new MutableLiveData<>(new DrugInPres());
        this.curUnit = new MutableLiveData<>();
        this.frequency = new MutableLiveData<>();
        this.listTime = new MutableLiveData<>(new ArrayList<>());
        this.specificDays = new ArrayList<>();
        this.dayBetween = 1;
        this.isEdited = false;
    }

    /* ============================== Drug =============================== */

    public LiveData<DrugInPres> getCurrentDrug() {
        return this.curDrug;
    }

    public DrugInPres getDrugToSave(String edtDate, String note) {
        DrugInPres drug = this.curDrug.getValue();
        drug.setUnit(this.curUnit.getValue());
        drug.setFrequency(this.frequency.getValue());
        drug.setSpecificDays(this.specificDays);
        drug.setEveryNDays(this.dayBetween);
        drug.setStartDate(edtDate);
        drug.setNote(note);
        drug.setTimeDosages(this.listTime.getValue());
        return drug;
    }

    public void setCurrentDrug(DrugInPres drugInPres) {
        this.curDrug.setValue(drugInPres);
        if(drugInPres.getFrequency() != null && this.frequency.getValue() == null ) {
            this.frequency.setValue(drugInPres.getFrequency());
        }

        if(drugInPres.getUnit() != null) {
            this.curUnit.setValue(drugInPres.getUnit());
        }

        if(drugInPres.getTimeDosages() != null && !drugInPres.getTimeDosages().isEmpty()) {
            this.listTime.setValue(drugInPres.getTimeDosages());
        }

        if(drugInPres.getEveryNDays() != null) {
            this.dayBetween = drugInPres.getEveryNDays();
        }

        if(drugInPres.getSpecificDays() != null ) {
            this.specificDays = drugInPres.getSpecificDays();
        }
    }

    public LiveData<String> drugName() {
        LiveData<String> name = new MutableLiveData<>(this.curDrug.getValue().getDrug().getName());
        return name;
    }

    /* ============================== Unit =============================== */

    public LiveData<Unit> getUnit() {
        return this.curUnit;
    }

    public void setUnit(Unit unit) {
        this.curUnit.setValue(unit);
    }

    /* =========================== Frequency ============================ */


    public LiveData<Frequency> getFrequency() {
        return this.frequency;
    }

    public void setFrequency (Frequency frequency) {
        this.frequency.setValue(frequency);
    }

    public Integer getDayBetween() {
        return this.dayBetween != null ? this.dayBetween : 1;
    }

    public void setFrequencyDaily() {
        this.frequency.setValue(Frequency.DAILY);
    }

    public void setFrequencySpecificDay(List<Integer> listId) {
        this.specificDays = listId;
        this.frequency.setValue(Frequency.SPECIFIC_DAYS);
    }

    public void setFrequencyEveryNDay(int count) {
        this.dayBetween = count;
        this.frequency.setValue(Frequency.EVERY_N_DAYS);
    }

    /* =========================== Time and Dosage ============================ */


    public LiveData<List<TimeDosage>> getListTimeDosage() {
        return this.listTime;
    }

    public void updateADosage() {
        this.listTime.setValue(this.listTime.getValue());
    }

    public void addTimeDosage(TimeDosage timeDosage) {
        List<TimeDosage> timeDosageList = this.listTime.getValue();
        timeDosageList.add(timeDosage);
        this.listTime.setValue(timeDosageList);
    }

    public void removeTimeDosage(int position) {
        List<TimeDosage> timeDosageList = this.listTime.getValue();
        if (timeDosageList != null) {
            timeDosageList.remove(position);
            this.listTime.setValue(timeDosageList);
        }
    }

    /* ============================== Note =============================== */

    public void setNote(String note) {
        DrugInPres drug = this.curDrug.getValue();
        if(drug != null) {
            drug.setNote(note);
            this.curDrug.setValue(drug);
        }
    }

}
