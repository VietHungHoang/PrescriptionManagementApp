package com.mad.prescriptionmanagementapp.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.mad.prescriptionmanagementapp.R;

import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    private Switch switchNhacNho;
    private Switch switchKhamBenh;
    private View layoutNhacNho;
    private CardView cardKhamBenh;
    private TextInputEditText edtTenDonThuoc, edtNgayBatDau, edtSoNgay, edtBenhVien, edtTenBacSi, edtNgayKham, edtNgayTaiKham;
    private Button btnThemThuoc, btnLuu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // Khởi tạo các view
        switchNhacNho = findViewById(R.id.switch_nhac_nho);
        switchKhamBenh = findViewById(R.id.switch_kham_benh);
        layoutNhacNho = findViewById(R.id.layout_nhac_nho);
        cardKhamBenh = findViewById(R.id.card_kham_benh);
        edtTenDonThuoc = findViewById(R.id.edt_ten_don_thuoc);
        edtNgayBatDau = findViewById(R.id.edt_ngay_bat_dau);
        edtSoNgay = findViewById(R.id.edt_so_ngay);
        edtBenhVien = findViewById(R.id.edt_benh_vien);
        edtTenBacSi = findViewById(R.id.edt_ten_bac_si);
        edtNgayKham = findViewById(R.id.edt_ngay_kham);
        edtNgayTaiKham = findViewById(R.id.edt_ngay_tai_kham);
        btnThemThuoc = findViewById(R.id.btn_them_thuoc);
        btnLuu = findViewById(R.id.btn_luu);
        ImageView btnClose = findViewById(R.id.btn_close);

        btnClose.setOnClickListener(v -> finish()); // Quay về trang trước
        // Set default visibility for the elements
        layoutNhacNho.setVisibility(View.GONE);  // Ban đầu ẩn layout nhắc nhở
        cardKhamBenh.setVisibility(View.GONE);   // Ban đầu ẩn card khám bệnh

        // Kiểm tra trạng thái của switch Nhắc nhở uống thuốc
        if (switchNhacNho.isChecked()) {
            layoutNhacNho.setVisibility(View.VISIBLE); // Hiển thị layout nhắc nhở
        } else {
            layoutNhacNho.setVisibility(View.GONE);    // Ẩn layout nếu không bật
        }

        // Kiểm tra trạng thái của switch Kham benh
        if (switchKhamBenh.isChecked()) {
            setKhamBenhEnabled(true);  // Kích hoạt các trường khám bệnh
        } else {
            setKhamBenhEnabled(false); // Tắt các trường khám bệnh
        }

        // Listener cho Switch "Nhắc nhở uống thuốc"
        switchNhacNho.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutNhacNho.setVisibility(View.VISIBLE);
            } else {
                layoutNhacNho.setVisibility(View.GONE);
            }
        });

        // Listener cho Switch "Thêm thông tin khám bệnh"
        switchKhamBenh.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setKhamBenhEnabled(true);
            } else {
                setKhamBenhEnabled(false);
            }
        });

        // Listener cho DatePicker khi click vào các trường ngày
        edtNgayBatDau.setOnClickListener(v -> showDatePicker(edtNgayBatDau));
        edtNgayKham.setOnClickListener(v -> {
            if (edtNgayKham.isEnabled()) {
                showDatePicker(edtNgayKham);
            }
        });
        edtNgayTaiKham.setOnClickListener(v -> {
            if (edtNgayTaiKham.isEnabled()) {
                showDatePicker(edtNgayTaiKham);
            }
        });

        // Listener cho các nút
        btnThemThuoc.setOnClickListener(v -> {
            // Xử lý thêm thuốc tại đây
        });

        btnLuu.setOnClickListener(v -> {
            // Xử lý lưu đơn thuốc tại đây
        });
        btnThemThuoc.setOnClickListener(v -> {
            Intent intent = new Intent(AddMedicineActivity.this, AddScheduleActivity.class);
            startActivity(intent);
        });

    }

    // Hàm để hiển thị DatePickerDialog
    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    // Hàm để kích hoạt hoặc tắt các trường thông tin khám bệnh
    private void setKhamBenhEnabled(boolean isEnabled) {
        cardKhamBenh.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        edtBenhVien.setEnabled(isEnabled);
        edtTenBacSi.setEnabled(isEnabled);
        edtNgayKham.setEnabled(isEnabled);
        edtNgayTaiKham.setEnabled(isEnabled);
    }
}
