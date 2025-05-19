package com.mad.prescriptionmanagementapp.api;

import android.telecom.Call;

import com.mad.prescriptionmanagementapp.model.PrescriptionGroup;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PrescriptionApi {
    @GET("api/v1/prescription/{status}")
    Call<List<PrescriptionGroup>> getPrescriptionsByStatus(@Path("status") int status);
}
