package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.model.Country;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface UserService {
    @POST("users/country")
    Call<ResponseObject<List<Country>>> getAllCountry();
}
