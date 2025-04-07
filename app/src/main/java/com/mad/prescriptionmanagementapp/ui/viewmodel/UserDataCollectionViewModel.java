package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mad.prescriptionmanagementapp.data.model.Country;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.repository.UserRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataCollectionViewModel extends AndroidViewModel {
    // Sử dụng MutableLiveData để giữ dữ liệu có thể thay đổi từ Fragment/Activity
    private final MutableLiveData<Long> roleId = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<Long> countryId = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    private final MutableLiveData<String> dob = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private final MutableLiveData<List<Country>> countries = new MutableLiveData<>();
    // Hoặc tạo một class/object để chứa tất cả dữ liệu
    // private final MutableLiveData<UserData> collectedData = new MutableLiveData<>(new UserData());

    // Các phương thức để Fragment cập nhật dữ liệue
    private final UserRepository userRepository;

    public UserDataCollectionViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository();
    }

    public MutableLiveData<List<Country>> getAllCountry() {
        return this.countries;
    }
    public void setRole(Long roleId) {
        this.roleId.setValue(roleId);
    }

    public MutableLiveData<String> getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public void setCountryId(Long countryId) {
        this.countryId.setValue(countryId);
    }

    public MutableLiveData<Long> getCountryId() {
        return this.countryId;
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.setValue(gender);
    }

    public MutableLiveData<String> getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob.setValue(dob);
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.setValue(phoneNumber);
    }

    public MutableLiveData<List<Country>> getCountries() {
        return countries;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public MutableLiveData<Long> getRoleId() {
        return this.roleId;
    }


    public void getUserCountry() {
        this.userRepository.getAllCountry(new Callback<ResponseObject<List<Country>>>() {
            @Override
            public void onResponse(Call<ResponseObject<List<Country>>> call, Response<ResponseObject<List<Country>>> response) {
                if(response.isSuccessful()) {
                    countries.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<List<Country>>> call, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

        });
    }

    // Phương thức để gọi API gửi dữ liệu
    public void submitDataToServer() {

    }
}