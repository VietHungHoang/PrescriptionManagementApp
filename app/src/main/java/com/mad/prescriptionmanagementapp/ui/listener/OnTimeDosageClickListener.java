package com.mad.prescriptionmanagementapp.ui.listener;

import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;

public interface OnTimeDosageClickListener {
    void onItemClick(TimeDosage timeDosage);
    void onRemoveButtonClick(int position);
}
