// ProfileActivity.java
package com.mad.prescriptionmanagementapp.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.R;
public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvGenderAge, tvHeight, tvWeight, tvBmi, tvBmr;
    private ImageButton btnUpdate;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Ánh xạ các view
        tvUsername = findViewById(R.id.user_name);
        tvGenderAge = findViewById(R.id.user_gender_age);
        tvWeight = findViewById(R.id.tv_weight);
        tvHeight = findViewById(R.id.tv_height);
        tvBmi = findViewById(R.id.tv_bmi);
        tvBmr = findViewById(R.id.tv_bmr);
        btnUpdate = findViewById(R.id.btn_update);

        // Dữ liệu giả lập
        user = new User("Duy Luân", "male", "01/01/2003", "0123456789", 58, 160);

        // Hiển thị dữ liệu
        updateUI();

        // Chuyển sang màn hình chỉnh sửa
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileSettingActivity.class);
            intent.putExtra("user", user);
            startActivityForResult(intent, 1);
        });
    }

    private void updateUI() {
        tvUsername.setText(user.getUsername());
        tvGenderAge.setText((user.getGender().equalsIgnoreCase("male") ? "Nam" : "Nữ") + "\n" +
                calculateAge(user.getBirthDate()) + " tuổi");
        tvWeight.setText("Cân nặng\n" + user.getWeight() + " kg");
        tvHeight.setText("Chiều cao\n" + user.getHeight() + " cm");
        tvBmi.setText("BMI\n" + String.format("%.2f", user.getBmi()));
        tvBmr.setText("BMR\n" + String.format("%.0f", user.getBmr()) + " kcal");
    }

    private int calculateAge(String birthDate) {
        try {
            String[] parts = birthDate.split("/");
            int birthYear = Integer.parseInt(parts[2]);
            int currentYear = java.time.Year.now().getValue();
            return currentYear - birthYear;
        } catch (Exception e) {
            return 22; // Giá trị mặc định
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            user = (User) data.getSerializableExtra("updated_user");
            updateUI();
        }
    }
}