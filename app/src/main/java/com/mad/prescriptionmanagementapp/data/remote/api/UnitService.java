package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UnitService {
    @GET("units/all")
    Call<ResponseObject<List<UnitResponse>>> getAllUnit();
}
