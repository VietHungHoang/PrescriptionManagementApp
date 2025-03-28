package com.mad.prescriptionmanagementapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.mad.prescriptionmanagementapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private TextView txtErrorEmail, txtErrorPassword;

    private AppCompatButton btnLogin;
    private TextView txtBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);

        // Automatically change the status bar color to match
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Mapping view
        this.edtEmail = findViewById(R.id.edtEmail);
        this.edtPassword = findViewById(R.id.edtPassword);
        this.txtErrorEmail = findViewById(R.id.txtErrorEmail);
        this.txtErrorPassword = findViewById(R.id.txtErrorPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.txtBtnRegister = findViewById(R.id.txtBtnRegister);

        this.btnLogin.setOnClickListener(v -> this.validateLogin());
        this.txtBtnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
            intent.putExtra("FRAGMENT_TYPE", "SELECT_USER_TYPE");
            startActivity(intent);
        });

    }

    private void validateLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean isValid = true;

        if (email.isEmpty()) {
            this.edtEmail.setBackgroundResource(R.drawable.edittext_bg_error);
            this.txtErrorEmail.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            this.edtEmail.setBackgroundResource(R.drawable.edittext_bg_normal);
            this.txtErrorEmail.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            this.edtPassword.setBackgroundResource(R.drawable.edittext_bg_error);
            this.txtErrorPassword.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            this.edtPassword.setBackgroundResource(R.drawable.edittext_bg_normal);
            this.txtErrorPassword.setVisibility(View.GONE);
        }

        if (isValid) {

        }
    }
}
