package com.mad.prescriptionmanagementapp.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.R;

public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // Ánh xạ nút quay lại
        ImageView btnBack = findViewById(R.id.imageView5);

        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }
}