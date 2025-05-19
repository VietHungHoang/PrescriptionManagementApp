package com.mad.prescriptionmanagementapp.data.repository;

import androidx.annotation.NonNull;

import com.mad.prescriptionmanagementapp.data.model.Country;
import com.mad.prescriptionmanagementapp.data.remote.NetworkClient;
import com.mad.prescriptionmanagementapp.data.remote.api.UserService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UserRepository {
    private final UserService userService;
    public UserRepository() {
        this.userService = NetworkClient.getUserService();
    }
    public void getAllCountry(@NonNull Callback<ResponseObject<List<Country>>> callback) {
        Call<ResponseObject<List<Country>>> call = userService.getAllCountry();
        call.enqueue(callback);
    }
}
