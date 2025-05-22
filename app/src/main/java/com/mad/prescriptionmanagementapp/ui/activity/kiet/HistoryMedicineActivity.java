package com.mad.prescriptionmanagementapp.ui.activity.kiet;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.ui.fragment.kiet.HistoryFragment;

public class HistoryMedicineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_history_kiet);

        // Load fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerHistory, new HistoryFragment())
                .commit();

        // Xử lý nút quay về
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Đóng activity hiện tại, quay về màn trước
        });
    }

}

