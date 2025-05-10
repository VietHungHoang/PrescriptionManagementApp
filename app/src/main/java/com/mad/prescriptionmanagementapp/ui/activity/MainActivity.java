package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.User;

public class MainActivity extends AppCompatActivity {
    private Button btnMedicineSearch, btnProfile;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        btnMedicineSearch = findViewById(R.id.btn_medicine_search);
        btnProfile = findViewById(R.id.btn_profile);

        // Tạo user mẫu (thay bằng dữ liệu từ backend hoặc đăng nhập sau này)
        user = new User();
        user.setId(1L); // ID phải khớp với database
        user.setName("Nguyen Van A");
        user.setDateOfBirth("26/05/2003");
        user.setPhoneNumber("0123456789");
        user.setGender("male");

        // Chuyển đến MedicineSearchActivity
        btnMedicineSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicineSearchActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        // Chuyển đến ProfileActivity
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }
}