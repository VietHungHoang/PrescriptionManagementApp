package com.mad.prescriptionmanagementapp.ui.listener;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;

public interface OnDrugClickListener{
    void onItemClick(DrugResponse drugResponse);
}
