package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.util.ErrorType;

import java.util.List;

public class AddPrescriptionInfoViewModel extends ViewModel {
    private final MutableLiveData<Boolean> medicalInfoSwitchState = new MutableLiveData<>(false);
    private final MutableLiveData<Pair<ErrorType, String>> errorMessage = new MutableLiveData<>();

    public LiveData<Pair<ErrorType, String>> getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(Pair<ErrorType, String> errorMessage) {
        this.errorMessage.setValue(errorMessage);
    }

    public LiveData<Boolean> getSwitchState() {
        return this.medicalInfoSwitchState;
    }

    public void setSwitchState(boolean state) {
        this.medicalInfoSwitchState.setValue(state);
    }


    public boolean validateAddPrescriptionInfo(Prescription pres, List<DrugInPres> drugInPresList) {
        if (pres.getName() == null || pres.getName().trim().isEmpty()) {
            this.errorMessage.setValue(new Pair<>(ErrorType.NAME_EMPTY, "Tên đơn thuốc không được để trống"));
            return false;

        } else {
            if (drugInPresList == null || drugInPresList.isEmpty()) {
                this.errorMessage.setValue(new Pair<>(ErrorType.NO_DRUG, "Vui lòng nhập ít nhất một thuốc"));
                return false;
            }
        }
        return true;
    }

//    public void handleBtnSavePres(Context context, PrescriptionRequest pres, List<DrugInPres> drugInPres) {
//        pres.setDrugs(drugInPres);
//        Executors.newSingleThreadExecutor().execute(() -> {
//            this.prescriptionRepository.insert(pres);
//            AlarmScheduler.scheduleAlarmsForPendingReminders(context);
//        });
//    }
}
