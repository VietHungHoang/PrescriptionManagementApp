package com.mad.prescriptionmanagementapp.ui.activity.dang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.RetrofitClient;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.model.UserSetting;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import android.util.Log;
public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvGenderAge, tvHeight, tvWeight, tvBmi, tvBmr;
    private ImageButton btnUpdate, btnBack;
    private LinearLayout layoutTermsOfService, layoutNotificationSettings,layoutLogout;
    private User user;
    private UserSetting userSetting;
    private ApiService apiService;

    // Định dạng ngày tháng
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Khởi tạo ApiService
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
        LinearLayout layoutTdee = findViewById(R.id.layout_tdee);
        // Ánh xạ nút Back
        btnBack = findViewById(R.id.btn_back);

        // Sự kiện nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng ProfileActivity
            }
        });
        // Đảm bảo layoutLogout đã được ánh xạ
        layoutLogout = findViewById(R.id.layout_logout);

        // Sự kiện nút Đăng Xuất
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

//       Sự kiện tính chỉ số TDEE
        layoutTdee.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TDEEActivity.class);
            try {
                String bmrText = tvBmr.getText().toString().replace("BMR\n", "").replace(" kcal", "");
                double bmrValue = Double.parseDouble(bmrText);
                intent.putExtra("bmr", bmrValue);
            } catch (NumberFormatException e) {
                Toast.makeText(ProfileActivity.this, "Lỗi: Không thể lấy giá trị BMR", Toast.LENGTH_SHORT).show();
                intent.putExtra("bmr", 1402.0); // Giá trị mặc định
            }
            startActivity(intent);
        });
        // Lấy dữ liệu user từ Intent
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            user = new User();
            user.setId(10L);
            user.setName("Phạm Hải Đăng");
            user.setDateOfBirth("26/05/2003");
            user.setPhoneNumber("0398066323");
            user.setGender("male");
            Toast.makeText(this, "No user data from Intent, using default", Toast.LENGTH_SHORT).show();
        } else if (user.getId() == null) {
            user.setId(10L);
            Toast.makeText(this, "User ID is null, using default ID: 1", Toast.LENGTH_SHORT).show();
        }

        // Tải dữ liệu từ backend
        loadUserData(user.getId());

        // Chuyển sang màn hình chỉnh sửa
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileSettingActivity.class);
            intent.putExtra("user", user);
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
        if (userId == null) {
            Toast.makeText(this, "User ID is null, cannot load data", Toast.LENGTH_SHORT).show();
            userSetting = new UserSetting(10L, user.getName(), user.getDateOfBirth(),
                    user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
            updateUI();
            return;
        }
        Call<ResponseObject<UserSetting>> call = apiService.getUserSetting(userId);
        call.enqueue(new Callback<ResponseObject<UserSetting>>() {
            @Override
            public void onResponse(Call<ResponseObject<UserSetting>> call, Response<ResponseObject<UserSetting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userSetting = response.body().getData();
                    Toast.makeText(ProfileActivity.this, "Tải dữ liệu người dùng thành công", Toast.LENGTH_SHORT).show();
                    if (userSetting.getDateOfBirth() != null) {
                        try {
                            LocalDate date = LocalDate.parse(userSetting.getDateOfBirth(), ISO_FORMATTER);
                            userSetting.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                            user.setDateOfBirth(date.format(DISPLAY_FORMATTER));
                        } catch (DateTimeParseException e) {
                            Toast.makeText(ProfileActivity.this, "Error parsing date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    user.setName(userSetting.getName());
                    user.setGender(userSetting.getGender());
                    user.setPhoneNumber(userSetting.getPhoneNumber());
                } else {
                    userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                            user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                    Toast.makeText(ProfileActivity.this, "No data from server, response code: " + response.code(), Toast.LENGTH_LONG).show();
                }
                updateUI();
            }

            @Override
            public void onFailure(Call<ResponseObject<UserSetting>> call, Throwable t) {
                Log.e("ProfileActivity", "Failed to load data: " + t.getMessage(), t);
                userSetting = new UserSetting(userId, user.getName(), user.getDateOfBirth(),
                        user.getPhoneNumber(), user.getGender(), 0.0, 0.0);
                updateUI();
                Toast.makeText(ProfileActivity.this, "Failed to load data: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void updateUI() {
        tvUsername.setText(user.getName() != null ? user.getName() : "N/A");
        tvGenderAge.setText((user.getGender() != null && user.getGender().equalsIgnoreCase("male") ? "Nam" : "Nữ") + "\n" +
                calculateAge(user.getDateOfBirth()) + " tuổi");

        Double weight = userSetting.getWeight() != null ? userSetting.getWeight() : 0.0;
        Double height = userSetting.getHeight() != null ? userSetting.getHeight() : 0.0;

        tvWeight.setText(String.format("Cân nặng\n%.1f kg", weight));
        tvHeight.setText(String.format("Chiều cao\n%.1f cm", height));

        double heightInMeters = height / 100.0;
        double bmi = (heightInMeters > 0) ? weight / (heightInMeters * heightInMeters) : 0.0;
        double bmr;
//        Đây là công thức Mifflin-St Jeor, được sử dụng để tính BMR (Basal Metabolic Rate),
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
            LocalDate date = LocalDate.parse(birthDate, DISPLAY_FORMATTER);
            LocalDate currentDate = LocalDate.now();
            return currentDate.getYear() - date.getYear();
        } catch (Exception e) {
            return 22;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            user = (User) data.getSerializableExtra("updated_user");
            Log.d("ProfileActivity", "Received updated_user: " + user + ", ID: " + (user != null ? user.getId() : "null"));
            if (user == null) {
                user = new User();
                user.setId(10L);
                Toast.makeText(this, "No updated user data from Intent, using default", Toast.LENGTH_SHORT).show();
            } else if (user.getId() == null) {
                user.setId(10L); // Gán ID mặc định nếu null
                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            }
            loadUserData(user.getId());
        }
    }
    private void showLogoutConfirmationDialog() {
        // Inflate layout XML
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout_confirmation, null);

        // Tìm các thành phần trong dialog
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Tạo dialog
        final AlertDialog dialog = builder.create();

        // Xử lý nút Xác nhận
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Đã xác nhận đăng xuất", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Xử lý nút Hủy
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Đã hủy đăng xuất", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Hiển thị dialog
        dialog.show();
    }
}