package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.model.UserSetting;
import com.mad.prescriptionmanagementapp.data.RetrofitClient; // Thay ApiClient bằng RetrofitClient
import com.mad.prescriptionmanagementapp.data.remote.api.ApiService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.ui.activity.dang.SuccessFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfileSettingActivity extends AppCompatActivity {
    private EditText etUsername, etBirthDate, etPhoneNumber, etWeight, etHeight;
    private RadioButton rbMale, rbFemale;
    private ImageButton btnUpdate, btnBack;
    private User user;
    private UserSetting userSetting;
    private ApiService apiService;

    // Định dạng ngày tháng
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Backend trả về ISO format (yyyy-MM-dd)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting);

        apiService = RetrofitClient.getApiService(); // Thay đổi này

        etUsername = findViewById(R.id.editText);
        etBirthDate = findViewById(R.id.editText2);
        etPhoneNumber = findViewById(R.id.editText3);
        etWeight = findViewById(R.id.editText4);
        etHeight = findViewById(R.id.editText5);
        rbMale = findViewById(R.id.radio_male);
        rbFemale = findViewById(R.id.radio_female);
        btnUpdate = findViewById(R.id.btn_update);
        btnBack = findViewById(R.id.btn_back);

        user = (User) getIntent().getSerializableExtra("user");

        loadUserSetting(user.getId());

        // Hiển thị dữ liệu từ user
        etUsername.setText(user.getName());
        if (user.getDateOfBirth() != null) {
            try {
                LocalDate date = LocalDate.parse(user.getDateOfBirth(), ISO_FORMATTER);
                etBirthDate.setText(date.format(DISPLAY_FORMATTER));
            } catch (DateTimeParseException e) {
                etBirthDate.setText(user.getDateOfBirth());
            }
        }
        etPhoneNumber.setText(user.getPhoneNumber());
        if (user.getGender() != null && user.getGender().equalsIgnoreCase("male")) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(true);
        }

        btnUpdate.setOnClickListener(v -> updateUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserSetting(Long userId) {
        Call<ResponseObject<UserSetting>> call = apiService.getUserSetting(userId);
        call.enqueue(new Callback<ResponseObject<UserSetting>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserSetting>> call, Response<ResponseObject<UserSetting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userSetting = response.body().getData();
                    if (userSetting.getDateOfBirth() != null) {
                        try {
                            LocalDate date = LocalDate.parse(userSetting.getDateOfBirth(), ISO_FORMATTER);
                            userSetting.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        } catch (DateTimeParseException e) {
                            // Giữ nguyên nếu không parse được
                        }
                    }
                    etWeight.setText(userSetting.getWeight() != null ? String.valueOf(userSetting.getWeight()) : "");
                    etHeight.setText(userSetting.getHeight() != null ? String.valueOf(userSetting.getHeight()) : "");
                } else {
                    userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                            user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                    Toast.makeText(ProfileSettingActivity.this, "No user setting data from server, response code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<UserSetting>> call, Throwable t) {
                userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                        user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                Toast.makeText(ProfileSettingActivity.this, "Lỗi khi tải cài đặt: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser() {
        String username = etUsername.getText().toString().trim();
        String birthDateStr = etBirthDate.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String gender = rbMale.isChecked() ? "male" : "female";

        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên người dùng");
            return;
        }
        if (birthDateStr.isEmpty()) {
            etBirthDate.setError("Vui lòng nhập ngày sinh");
            return;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (weightStr.isEmpty()) {
            etWeight.setError("Vui lòng nhập cân nặng");
            return;
        }
        if (heightStr.isEmpty()) {
            etHeight.setError("Vui lòng nhập chiều cao");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            LocalDate birthDate = LocalDate.parse(birthDateStr, DISPLAY_FORMATTER);
            String birthDateIso = birthDate.format(ISO_FORMATTER);

            user.setName(username);
            user.setDateOfBirth(birthDateIso);
            user.setPhoneNumber(phoneNumber);
            user.setGender(gender);

            if (userSetting == null) {
                userSetting = new UserSetting(user.getId(), username, birthDateIso, phoneNumber, gender, weight, height);
            } else {
                userSetting.setName(username);
                userSetting.setDateOfBirth(birthDateIso);
                userSetting.setPhoneNumber(phoneNumber);
                userSetting.setGender(gender);
                userSetting.setWeight(weight);
                userSetting.setHeight(height);
            }

            updateUserToBackend(user);
            updateUserSettingToBackend(userSetting);

            user.updateCalculations();

            showSuccessFragment();

        } catch (NumberFormatException e) {
            if (!weightStr.matches("\\d+(\\.\\d+)?")) {
                etWeight.setError("Vui lòng nhập số hợp lệ");
            }
            if (!heightStr.matches("\\d+(\\.\\d+)?")) {
                etHeight.setError("Vui lòng nhập số hợp lệ");
            }
        } catch (DateTimeParseException e) {
            etBirthDate.setError("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
        }
    }

    private void updateUserToBackend(User user) {
        Call<User> call = apiService.updateUser(user.getId(), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    if (updatedUser.getDateOfBirth() != null) {
                        try {
                            LocalDate date = LocalDate.parse(updatedUser.getDateOfBirth(), ISO_FORMATTER);
                            updatedUser.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        } catch (DateTimeParseException e) {
                            // Giữ nguyên nếu không parse được
                        }
                    }
                    ProfileSettingActivity.this.user = updatedUser;
                } else {
                    Log.e("ProfileSetting", "Update user failed: " + response.code());
                    Toast.makeText(ProfileSettingActivity.this, "Lỗi khi cập nhật người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ProfileSetting", "Lỗi khi gọi updateUser API", t);
                Toast.makeText(ProfileSettingActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserSettingToBackend(UserSetting userSetting) {
        Call<UserSetting> call = apiService.updateUserSetting(userSetting.getUserId(), userSetting);
        call.enqueue(new Callback<UserSetting>() {
            @Override
            public void onResponse(Call<UserSetting> call, Response<UserSetting> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserSetting updatedSetting = response.body();
                    if (updatedSetting.getDateOfBirth() != null) {
                        try {
                            LocalDate date = LocalDate.parse(updatedSetting.getDateOfBirth(), ISO_FORMATTER);
                            updatedSetting.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        } catch (DateTimeParseException e) {
                            // Giữ nguyên nếu không parse được
                        }
                    }
                    ProfileSettingActivity.this.userSetting = updatedSetting;
                } else {
                    Log.e("ProfileSetting", "Update userSetting failed: " + response.code());
                    Toast.makeText(ProfileSettingActivity.this, "Lỗi khi cập nhật cài đặt", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSetting> call, Throwable t) {
                Log.e("ProfileSetting", "Lỗi khi gọi updateUserSetting API", t);
                Toast.makeText(ProfileSettingActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessFragment() {
        SuccessFragment successFragment = new SuccessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_setting, successFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent resultIntent = new Intent();
            Log.d("ProfileSettingActivity", "Sending updated_user: " + user + ", ID: " + (user != null ? user.getId() : "null"));
            resultIntent.putExtra("updated_user", user);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000);
    }
}