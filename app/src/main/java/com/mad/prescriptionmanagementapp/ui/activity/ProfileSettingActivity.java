package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.User;

public class ProfileSettingActivity extends AppCompatActivity {
    private EditText etUsername, etBirthDate, etPhoneNumber, etWeight, etHeight;
    private RadioButton rbMale, rbFemale;
    private ImageButton btnUpdate, btnBack;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting);

        // Ánh xạ các view
        etUsername = findViewById(R.id.editText);
        etBirthDate = findViewById(R.id.editText2);
        etPhoneNumber = findViewById(R.id.editText3);
        etWeight = findViewById(R.id.editText4);
        etHeight = findViewById(R.id.editText5);
        rbMale = findViewById(R.id.radio_male);
        rbFemale = findViewById(R.id.radio_female);
        btnUpdate = findViewById(R.id.btn_update);
        btnBack = findViewById(R.id.btn_back);

        // Lấy dữ liệu từ Intent
        user = (User) getIntent().getSerializableExtra("user");

        // Hiển thị dữ liệu
        etUsername.setText(user.getUsername());
        etBirthDate.setText(user.getBirthDate());
        etPhoneNumber.setText(user.getPhoneNumber());
        etWeight.setText(String.valueOf(user.getWeight()));
        etHeight.setText(String.valueOf(user.getHeight()));
        if (user.getGender().equalsIgnoreCase("male")) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(true);
        }

        // Xử lý nút cập nhật
        btnUpdate.setOnClickListener(v -> updateUser());

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateUser() {
        try {
            // Lấy dữ liệu từ giao diện
            String username = etUsername.getText().toString();
            String birthDate = etBirthDate.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            double weight = Double.parseDouble(etWeight.getText().toString());
            double height = Double.parseDouble(etHeight.getText().toString());
            String gender = rbMale.isChecked() ? "male" : "female";

            // Cập nhật user
            user.setUsername(username);
            user.setBirthDate(birthDate);
            user.setPhoneNumber(phoneNumber);
            user.setWeight(weight);
            user.setHeight(height);
            user.setGender(gender);
            user.updateCalculations(); // Tính lại BMI và BMR

            // Hiển thị SuccessFragment
            showSuccessFragment();

        } catch (NumberFormatException e) {
            // Hiển thị thông báo lỗi nếu dữ liệu không hợp lệ
            etWeight.setError("Vui lòng nhập số hợp lệ");
            etHeight.setError("Vui lòng nhập số hợp lệ");
        }
    }

    private void showSuccessFragment() {
        SuccessFragment successFragment = new SuccessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_setting, successFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Sau khi SuccessFragment đóng (sau 2 giây), trả dữ liệu và đóng Activity
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_user", user);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000);
    }
}