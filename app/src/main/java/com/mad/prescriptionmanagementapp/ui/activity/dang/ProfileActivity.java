package com.mad.prescriptionmanagementapp.ui.activity.dang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.RetrofitClient;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.model.UserSetting;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.api.ApiService;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvGenderAge, tvHeight, tvWeight, tvBmi, tvBmr;
    private ImageButton btnUpdate, btnBack;
    private LinearLayout layoutTermsOfService, layoutNotificationSettings, layoutLogout;
    private User user;
    private UserSetting userSetting;
    private ApiService apiService;

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        apiService = RetrofitClient.getApiService();

        // Ánh xạ các view
        tvUsername = findViewById(R.id.user_name);
        tvGenderAge = findViewById(R.id.user_gender_age);
        tvWeight = findViewById(R.id.tv_weight);
        tvHeight = findViewById(R.id.tv_height);
        tvBmi = findViewById(R.id.tv_bmi);
        tvBmr = findViewById(R.id.tv_bmr);
        btnUpdate = findViewById(R.id.btn_update);
        layoutTermsOfService = findViewById(R.id.layout_terms_of_service);
        layoutNotificationSettings = findViewById(R.id.layout_notification_settings);
        layoutLogout = findViewById(R.id.layout_logout);
        btnBack = findViewById(R.id.btn_back);

        // Sự kiện nút Back
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện nút Đăng Xuất
        layoutLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Sự kiện tính chỉ số TDEE
        LinearLayout layoutTdee = findViewById(R.id.layout_tdee);
        layoutTdee.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TDEEActivity.class);
            try {
                String bmrText = tvBmr.getText().toString().replace("BMR\n", "").replace(" kcal", "");
                double bmrValue = Double.parseDouble(bmrText);
                intent.putExtra("bmr", bmrValue);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Lỗi: Không thể lấy giá trị BMR", Toast.LENGTH_SHORT).show();
                intent.putExtra("bmr", 1402.0);
            }
            startActivity(intent);
        });

        // Lấy dữ liệu user từ Intent
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null || user.getId() == null) {
            user = new User();
            user.setId(1L);
            user.setName("Phạm Hải Đăng");
            user.setDateOfBirth("2003-05-26"); // Sử dụng định dạng ISO
            user.setPhoneNumber("0398066323");
            user.setGender("male");
            Toast.makeText(this, "Không có dữ liệu người dùng từ Intent, sử dụng mặc định", Toast.LENGTH_SHORT).show();
        }

        // Tải dữ liệu từ backend
        loadUserData(user.getId());

        // Chuyển sang màn hình chỉnh sửa
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileSettingActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("userSetting", userSetting);
            startActivityForResult(intent, 1);
        });

        // Xử lý sự kiện click vào mục "Điều Khoản Dịch Vụ"
        layoutTermsOfService.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TermsOfServiceActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click vào mục "Cài Đặt Thông Báo"
        layoutNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData(Long userId) {
        Call<ResponseObject<UserSetting>> call = apiService.getUserSetting(userId);
        call.enqueue(new Callback<ResponseObject<UserSetting>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserSetting>> call, Response<ResponseObject<UserSetting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userSetting = response.body().getData();
                    // Đồng bộ dữ liệu với User
                    user.setName(userSetting.getName());
                    user.setGender(userSetting.getGender());
                    user.setPhoneNumber(userSetting.getPhoneNumber());
                    if (userSetting.getDateOfBirth() != null) {
                        try {
                            LocalDate date = LocalDate.parse(userSetting.getDateOfBirth(), ISO_FORMATTER);
                            user.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                            userSetting.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        } catch (DateTimeParseException e) {
                            Log.e("ProfileActivity", "Lỗi parse ngày sinh: " + e.getMessage());
                        }
                    }
                    Toast.makeText(ProfileActivity.this, "Tải dữ liệu người dùng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                            user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                    Toast.makeText(ProfileActivity.this, "Không có dữ liệu cài đặt từ server, mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
                updateUI();
            }

            @Override
            public void onFailure(Call<ResponseObject<UserSetting>> call, Throwable t) {
                Log.e("ProfileActivity", "Lỗi khi tải dữ liệu: " + t.getMessage(), t);
                userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                        user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                updateUI();
                Toast.makeText(ProfileActivity.this, "Lỗi khi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI() {
        tvUsername.setText(user.getName() != null ? user.getName() : "N/A");
        String genderText = (user.getGender() != null && user.getGender().equalsIgnoreCase("male")) ? "Nam" : "Nữ";
        tvGenderAge.setText(genderText + "\n" + calculateAge(user.getDateOfBirth()) + " tuổi");

        Double weight = userSetting.getWeight() != null ? userSetting.getWeight() : 0.0;
        Double height = userSetting.getHeight() != null ? userSetting.getHeight() : 0.0;

        tvWeight.setText(String.format("Cân nặng\n%.1f kg", weight));
        tvHeight.setText(String.format("Chiều cao\n%.1f cm", height));

        double heightInMeters = height / 100.0;
        double bmi = (heightInMeters > 0) ? weight / (heightInMeters * heightInMeters) : 0.0;
        double bmr;
        if (user.getGender() != null && user.getGender().equalsIgnoreCase("male")) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * calculateAge(user.getDateOfBirth()));
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * calculateAge(user.getDateOfBirth()));
        }

        tvBmi.setText(String.format("BMI\n%.2f kg/m²", bmi));
        tvBmr.setText(String.format("BMR\n%.0f kcal", bmr));
    }

    private int calculateAge(String birthDate) {
        try {
            LocalDate date = LocalDate.parse(birthDate, birthDate.contains("/") ? DISPLAY_FORMATTER : ISO_FORMATTER);
            return LocalDate.now().getYear() - date.getYear();
        } catch (Exception e) {
            Log.e("ProfileActivity", "Lỗi tính tuổi: " + e.getMessage());
            return 22;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            user = (User) data.getSerializableExtra("updated_user");
            userSetting = (UserSetting) data.getSerializableExtra("updated_user_setting");
            if (user == null || user.getId() == null) {
                user = new User();
                user.setId(1L);
                Toast.makeText(this, "Không có dữ liệu người dùng cập nhật, sử dụng mặc định", Toast.LENGTH_SHORT).show();
            }
            if (userSetting == null) {
                userSetting = new UserSetting(user.getId(), user.getName(), user.getDateOfBirth(),
                        user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
            }
            updateUI();
        }
    }

    private void showLogoutConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout_confirmation, null);
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        tvConfirm.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Đã xác nhận đăng xuất", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // Thêm logic đăng xuất nếu cần
        });

        tvCancel.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Đã hủy đăng xuất", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}