package com.mad.prescriptionmanagementapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ListPrescriptionActivity extends AppCompatActivity {

    private MaterialButton btnRemind, btnNoRemind, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prescription);

        // Khởi tạo các button
        btnRemind = findViewById(R.id.btn_remind);
        btnNoRemind = findViewById(R.id.btn_no_remind);
        btnDone = findViewById(R.id.btn_done);

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.tabGroup);

        // Lắng nghe sự kiện chọn tab
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Cập nhật trạng thái màu nền và văn bản khi một tab được chọn
                updateTabState(btnRemind, checkedId == R.id.btn_remind, true);
                updateTabState(btnNoRemind, checkedId == R.id.btn_no_remind, false);
                updateTabState(btnDone, checkedId == R.id.btn_done, false);

                // Hiển thị mục dựa trên tab được chọn
                if (isChecked) {
                    if (checkedId == R.id.btn_remind) {
                        // Hiển thị các mục nhắc nhở
                    } else if (checkedId == R.id.btn_no_remind) {
                        // Hiển thị các mục không nhắc nhở
                    } else if (checkedId == R.id.btn_done) {
                        // Hiển thị các mục đã xong
                    }
                }
            }
        });
    }

    // Phương thức cập nhật màu sắc cho các tab
    private void updateTabState(MaterialButton button, boolean isSelected, boolean isFirstTab) {
        if (isSelected) {
            button.setBackgroundColor(getResources().getColor(R.color.tab_selected_bg_color));
            button.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.tab_unselected_bg_color));
            button.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
        }
    }

    // Đặt lại màu nền tab về mặc định (nếu cần)
    private void resetTabColors() {
        int defaultColor = Color.WHITE;
        int defaultTextColor = Color.BLACK;

        btnRemind.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        btnRemind.setTextColor(defaultTextColor);

        btnNoRemind.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        btnNoRemind.setTextColor(defaultTextColor);

        btnDone.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        btnDone.setTextColor(defaultTextColor);
    }
}
