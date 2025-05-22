package com.mad.prescriptionmanagementapp.ui.activity.dang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.RetrofitClient;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.model.UserResponse;
import com.mad.prescriptionmanagementapp.data.model.UserSetting;
import com.mad.prescriptionmanagementapp.data.remote.api.ApiService;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class ProfileSettingActivity extends AppCompatActivity {
    private EditText etUsername, etBirthDate, etPhoneNumber, etWeight, etHeight;
    private RadioButton rbMale, rbFemale;
    private ImageButton btnUpdate, btnBack;
    private User user;
    private UserSetting userSetting;
    private ApiService apiService;

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting);

        apiService = RetrofitClient.getApiService();

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
        userSetting = (UserSetting) getIntent().getSerializableExtra("userSetting");

        if (user == null || user.getId() == null) {
            user = new User();
            user.setId(1L);
            Toast.makeText(this, "Không có dữ liệu người dùng, sử dụng mặc định", Toast.LENGTH_SHORT).show();
        }

        if (userSetting == null) {
            loadUserSetting(user.getId());
        } else {
            displayUserData();
        }

        btnUpdate.setOnClickListener(v -> updateUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void displayUserData() {
        etUsername.setText(user.getName() != null ? user.getName() : "");
        if (user.getDateOfBirth() != null) {
            try {
                LocalDate date = LocalDate.parse(user.getDateOfBirth(), user.getDateOfBirth().contains("/") ? DISPLAY_FORMATTER : ISO_FORMATTER);
                etBirthDate.setText(date.format(DISPLAY_FORMATTER));
            } catch (DateTimeParseException e) {
                etBirthDate.setText(user.getDateOfBirth());
                Log.e("ProfileSetting", "Lỗi parse ngày sinh: " + e.getMessage());
            }
        }
        etPhoneNumber.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        etWeight.setText(userSetting.getWeight() != null ? String.valueOf(userSetting.getWeight()) : "");
        etHeight.setText(userSetting.getHeight() != null ? String.valueOf(userSetting.getHeight()) : "");
        if (user.getGender() != null && user.getGender().equalsIgnoreCase("male")) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(true);
        }
    }

    private void loadUserSetting(Long userId) {
        if (userId == null) {
            userSetting = new UserSetting(user.getId(), user.getName(), user.getDateOfBirth(),
                    user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
            displayUserData();
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseObject<UserSetting>> call = apiService.getUserSetting(userId);
        call.enqueue(new Callback<ResponseObject<UserSetting>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserSetting>> call, Response<ResponseObject<UserSetting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userSetting = response.body().getData();
                    if (userSetting.getUserId() == null) {
                        userSetting.setUserId(userId);
                    }
                    displayUserData();
                } else {
                    userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                            user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                    displayUserData();
                    Toast.makeText(ProfileSettingActivity.this, "Không có dữ liệu cài đặt từ server, mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<UserSetting>> call, Throwable t) {
                userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                        user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                displayUserData();
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

            if (user.getId() == null) {
                Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setName(username);
            user.setDateOfBirth(birthDateIso);
            user.setPhoneNumber(phoneNumber);
            user.setGender(gender);

            if (userSetting == null) {
                userSetting = new UserSetting();
                userSetting.setUserId(user.getId());
            } else if (userSetting.getUserId() == null) {
                userSetting.setUserId(user.getId());
            }
            userSetting.setName(username);
            userSetting.setDateOfBirth(birthDateIso);
            userSetting.setPhoneNumber(phoneNumber);
            userSetting.setGender(gender);
            userSetting.setWeight(weight);
            userSetting.setHeight(height);

            updateUserToBackend(user, userSetting);

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

    private void updateUserToBackend(User user, UserSetting userSetting) {
        Call<ResponseObject<UserResponse>> callUser = apiService.updateUser(user.getId(), user);
        callUser.enqueue(new Callback<ResponseObject<UserResponse>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserResponse>> call, Response<ResponseObject<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    UserResponse userResponse = response.body().getData();
                    ProfileSettingActivity.this.user = userResponse.toUser();
                    try {
                        if (userResponse.getDateOfBirth() != null) {
                            LocalDate date = LocalDate.parse(userResponse.getDateOfBirth(), ISO_FORMATTER);
                            ProfileSettingActivity.this.user.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        }
                    } catch (DateTimeParseException e) {
                        Log.e("ProfileSetting", "Lỗi parse ngày sinh từ phản hồi: " + e.getMessage());
                    }
                    updateUserSettingToBackend(userSetting);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Không có dữ liệu phản hồi";
                    Log.e("ProfileSetting", "Cập nhật user thất bại: " + response.code() + ", " + errorMsg);
                    Toast.makeText(ProfileSettingActivity.this, "Lỗi khi cập nhật người dùng: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<UserResponse>> call, Throwable t) {
                Log.e("ProfileSetting", "Lỗi khi gọi updateUser API: " + t.getMessage());
                Toast.makeText(ProfileSettingActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserSettingToBackend(UserSetting userSetting) {
        if (userSetting.getUserId() == null) {
            Log.e("ProfileSetting", "Lỗi: userSetting.getUserId() là null");
            Toast.makeText(ProfileSettingActivity.this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseObject<UserSetting>> callSetting = apiService.updateUserSetting(userSetting.getUserId(), userSetting);
        callSetting.enqueue(new Callback<ResponseObject<UserSetting>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserSetting>> call, Response<ResponseObject<UserSetting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ProfileSettingActivity.this.userSetting = response.body().getData();
                    try {
                        if (userSetting.getDateOfBirth() != null) {
                            LocalDate date = LocalDate.parse(userSetting.getDateOfBirth(), ISO_FORMATTER);
                            ProfileSettingActivity.this.userSetting.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        }
                    } catch (DateTimeParseException e) {
                        Log.e("ProfileSetting", "Lỗi parse ngày sinh từ phản hồi: " + e.getMessage());
                    }
                    showSuccessFragment();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Không có dữ liệu phản hồi";
                    Log.e("ProfileSetting", "Cập nhật userSetting thất bại: " + response.code() + ", " + errorMsg);
                    Toast.makeText(ProfileSettingActivity.this, "Lỗi khi cập nhật cài đặt: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<UserSetting>> call, Throwable t) {
                Log.e("ProfileSetting", "Lỗi khi gọi updateUserSetting API: " + t.getMessage());
                Toast.makeText(ProfileSettingActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessFragment() {
        // Hiển thị Toast thông báo cập nhật thành công
        Toast.makeText(ProfileSettingActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

        SuccessFragment successFragment = new SuccessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_setting, successFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_user", user);
            resultIntent.putExtra("updated_user_setting", userSetting);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000);
    }
}