package com.mad.prescriptionmanagementapp.ui.listener;

import com.mad.prescriptionmanagementapp.data.model.TimeDosage;

public interface OnTimeDosageClickListener {
    void onItemClick(TimeDosage timeDosage);
    void onRemoveButtonClick(int position);
}
