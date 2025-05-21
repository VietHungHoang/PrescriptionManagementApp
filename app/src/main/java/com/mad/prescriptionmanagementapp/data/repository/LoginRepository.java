package com.mad.prescriptionmanagementapp.data.repository;


import androidx.annotation.NonNull;

// import com.yourcompany.yourapp.data.remote.dto.AuthRequest; // Không cần nữa

import com.mad.prescriptionmanagementapp.data.remote.dto.request.CustomerRegisterGoogleRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.LoginResponse;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.LoginService;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.GoogleLoginRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.GoogleAuthRespone;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginRepository {

    private final LoginService loginService;

    public LoginRepository() {
        this.loginService = NetworkClient.getAuthService();
    }

    public void loginGoogle(String idToken, @NonNull Callback<GoogleAuthRespone> callback) {
        GoogleLoginRequest loginRequest = new GoogleLoginRequest(idToken);
        Call<GoogleAuthRespone> call = loginService.loginWithGoogle(loginRequest);
        call.enqueue(callback);
    }

    public void registerGoogle(CustomerRegisterGoogleRequest request, @NonNull Callback<ResponseObject<LoginResponse>> callback) {
        Call<ResponseObject<LoginResponse>> call = loginService.registerWithGoogle(request);
        call.enqueue(callback);
    }

    public void checkToken(String accessToken, @NonNull Callback<ResponseObject<String>> callback) {
        Call<ResponseObject<String>> call = loginService.checkTokenValid("Bearer " + accessToken);
        call.enqueue(callback);
    }
}