package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DrugService {

    @GET("drugs/simple")
    Call<ResponseObject<List<DrugResponse>>> getDrugsSimple();

    @GET("drugs/unit")
    Call<ResponseObject<List<UnitResponse>>> getAllUnit();
}