package com.mad.prescriptionmanagementapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class MedicineScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_schedule);

        // Thêm CalendarFragment vào calendarContainer
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.calendarContainer, new FragmentCalendar());
        transaction.commit();

        // Xử lý các tab
        MaterialButtonToggleGroup tabGroup = findViewById(R.id.tabGroup);
        MaterialButton btnRemind = findViewById(R.id.btn_remind);
        MaterialButton btnNoRemind = findViewById(R.id.btn_no_remind);
        MaterialButton btnDone = findViewById(R.id.btn_done);

        tabGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Cập nhật màu nền và màu chữ khi một tab được chọn
                updateTabState(btnRemind, checkedId == R.id.btn_remind, true);
                updateTabState(btnNoRemind, checkedId == R.id.btn_no_remind, false);
                updateTabState(btnDone, checkedId == R.id.btn_done, false);
            }
        });
    }

    // Hàm cập nhật trạng thái màu sắc của tab khi được chọn hoặc không được chọn
    private void updateTabState(MaterialButton button, boolean isSelected, boolean isFirstTab) {
        if (isSelected) {
            button.setBackgroundColor(getResources().getColor(R.color.tab_selected_bg_color));
            button.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.tab_unselected_bg_color));
            button.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
        }
    }
}
