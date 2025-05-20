package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.remote.dto.request.CustomerRegisterGoogleRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.LoginResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.GoogleLoginRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.GoogleAuthRespone;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginService {

    @POST("users/google/verify")
    Call<GoogleAuthRespone> loginWithGoogle(
            @Body GoogleLoginRequest request);

    @POST("users/register/google")
    Call<ResponseObject<LoginResponse>> registerWithGoogle(
            @Body CustomerRegisterGoogleRequest request);

    @GET("users/check-token")
    Call<ResponseObject<Void>> checkTokenValid(
            @Header("Authorization") String accessToken);

}