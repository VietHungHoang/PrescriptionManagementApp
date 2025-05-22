package com.mad.prescriptionmanagementapp.data.remote.api.kiet;

import retrofit2.Call;


import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.PrescriptionResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PrescriptionApi {
    @GET("api/v1/prescriptions/{status}")
    Call<List<PrescriptionResponse>> getPrescriptionsByStatus(@Path("status") int status);
}
