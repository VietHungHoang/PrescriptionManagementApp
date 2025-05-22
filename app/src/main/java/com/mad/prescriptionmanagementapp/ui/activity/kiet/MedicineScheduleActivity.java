package com.mad.prescriptionmanagementapp.ui.activity.kiet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.ui.fragment.kiet.FragmentCalendar;
import com.mad.prescriptionmanagementapp.ui.fragment.kiet.MedicineFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MedicineScheduleActivity extends AppCompatActivity {
    private MedicineFragment medicineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_schedule_kiet);
        // Thêm sự kiện nút back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();  // Hoặc finish();
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Thêm FragmentCalendar vào calendarContainer
        FragmentCalendar fragmentCalendar = new FragmentCalendar();
        fragmentCalendar.setOnDateSelectedListener(this::onDateSelected);
        transaction.replace(R.id.calendarContainer, fragmentCalendar);

        // Thêm MedicineFragment vào fragment_container
        if (savedInstanceState == null) {
            medicineFragment = new MedicineFragment();
            transaction.replace(R.id.fragment_container, medicineFragment);
        } else {
            medicineFragment = (MedicineFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        }
        transaction.commit();

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_invoice) {
                Intent intent = new Intent(MedicineScheduleActivity.this, ListPrescriptionActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (medicineFragment != null) {
            medicineFragment.updateMedicineList(todayDate);
        }
    }

    // Xử lý khi chọn ngày trong lịch
    private void onDateSelected(String selectedDate) {
        if (medicineFragment != null) {
            medicineFragment.updateMedicineList(selectedDate);
        }
    }
}
