package com.mad.prescriptionmanagementapp.ui.activity.hung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);

        LoginRepository loginRepository = new LoginRepository();
        loginRepository.checkToken(sharedPrefUtils.getToken(), new Callback<ResponseObject<String>>() {
            @Override
            public void onResponse(Call<ResponseObject<String>> call, Response<ResponseObject<String>> response) {
                Intent intent;

                if(response.isSuccessful() && response.body() != null) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                    sharedPrefUtils.saveName(response.body().getData());
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    sharedPrefUtils.saveToken(null, null);
                }

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                SplashActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ResponseObject<String>> call, Throwable throwable) {
                Toast.makeText(SplashActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

