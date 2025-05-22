package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.model.UserResponse;
import com.mad.prescriptionmanagementapp.data.model.UserSetting;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("users/{id}/settings")
    Call<ResponseObject<UserSetting>> getUserSetting(@Path("id") Long id);

    @PUT("users/{id}")
    Call<ResponseObject<UserResponse>> updateUser(@Path("id") Long id, @Body User user);

    @PUT("users/{id}/settings")
    Call<ResponseObject<UserSetting>> updateUserSetting(@Path("id") Long id, @Body UserSetting userSetting);
}