package com.mad.prescriptionmanagementapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.fragment.LoginFragment;
import com.mad.prescriptionmanagementapp.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Mở LoginFragment mặc định
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }

    // Hàm chuyển sang RegisterFragment
    public void openRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null) // Cho phép quay lại LoginFragment khi nhấn Back
                .commit();
    }
}
