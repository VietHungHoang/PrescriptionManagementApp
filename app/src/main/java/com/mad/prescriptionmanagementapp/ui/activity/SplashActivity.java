package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.repository.LoginRepository;
import com.mad.prescriptionmanagementapp.util.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private SharedPrefUtils sharedPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);

        this.sharedPrefUtils = new SharedPrefUtils(this);

        LoginRepository loginRepository = new LoginRepository();
        loginRepository.checkToken(this.sharedPrefUtils.getToken(), new Callback<ResponseObject<Void>>() {
            @Override
            public void onResponse(Call<ResponseObject<Void>> call, Response<ResponseObject<Void>> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseObject<Void>> call, Throwable throwable) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            }
        });

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, this.sharedPrefUtils.getToken() != null ? HomeActivity.class : LoginActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            finish();
//        }, 2000);
    }
}

