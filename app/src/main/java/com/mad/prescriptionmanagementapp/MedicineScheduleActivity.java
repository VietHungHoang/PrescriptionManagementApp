package com.mad.prescriptionmanagementapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MedicineScheduleActivity extends AppCompatActivity {

    // Gọi khi Activity được tạo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_schedule);

        // Hiển thị Fragment chứa thông tin thuốc
        loadMedicineFragment();

        // Xử lý FloatingActionButton - Thêm mới phiên thuốc
//        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
//        fabAdd.setOnClickListener(view -> {
//            // Mở dialog hoặc activity thêm mới
//            showAddMedicineDialog();
//        });
    }

    // Hàm load Fragment vào container
    private void loadMedicineFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MedicineItemFragment medicineFragment = new MedicineItemFragment(); // Tên Fragment bạn đã tạo
        fragmentTransaction.replace(R.id.fragmentMedicineContainer, medicineFragment);
        fragmentTransaction.commit();
    }

//    // Hiển thị dialog thêm mới (có thể thay đổi tùy chức năng)
//    private void showAddMedicineDialog() {
//        Dialog dialog = new Dialog(MedicineScheduleActivity.this);
//        dialog.setContentView(R.layout.dialog_add_medicine); // Layout thêm mới thuốc bạn tạo
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        Button btnClose = dialog.findViewById(R.id.btnClose);
//        btnClose.setOnClickListener(view -> dialog.dismiss());
//
//        dialog.show();
//    }
}
