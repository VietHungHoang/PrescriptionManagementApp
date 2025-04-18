package com.mad.prescriptionmanagementapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.prescriptionmanagementapp.R;

public class MedicineScheduleActivity extends AppCompatActivity {
    private MedicineFragment medicineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_schedule);

        // Khởi tạo FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        // Thêm FragmentCalendar vào calendarContainer
        FragmentCalendar fragmentCalendar = new FragmentCalendar();
        fragmentCalendar.setOnDateSelectedListener(this::onDateSelected); // Nhận ngày từ lịch
        transaction.replace(R.id.calendarContainer, fragmentCalendar);

        // Thêm MedicineFragment vào fragment_container
        if (savedInstanceState == null) {
            medicineFragment = new MedicineFragment();
            transaction.replace(R.id.fragment_container, medicineFragment);
        } else {
            medicineFragment = (MedicineFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        }

        transaction.commit();
        // Bắt sự kiện khi nhấn vào menu
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_invoice) {
                    // Chuyển sang ListPrescriptionActivity
                    Intent intent = new Intent(MedicineScheduleActivity.this, ListPrescriptionActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    // Xử lý khi chọn ngày trong lịch
    private void onDateSelected(String selectedDate) {
        if (medicineFragment != null) {
            medicineFragment.updateMedicineList(selectedDate);
        }
    }
}
