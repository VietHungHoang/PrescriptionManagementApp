package com.mad.prescriptionmanagementapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.R;

public class TDEEActivity extends AppCompatActivity {
    private TextView tvBmr, tvTdee;
    private Spinner spinnerActivityLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdee);

        // Thiết lập nút quay lại
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish()); // Quay lại ProfileActivity

        // Ánh xạ các view
        tvBmr = findViewById(R.id.tv_bmr_value);
        tvTdee = findViewById(R.id.tv_tdee_result);
        spinnerActivityLevel = findViewById(R.id.spinner_activity_level);

        // Lấy BMR từ Intent và validate
        double bmr = getIntent().getDoubleExtra("bmr", 0.0);
        if (bmr <= 0) {
            Toast.makeText(this, "BMR không hợp lệ. Vui lòng cập nhật hồ sơ.", Toast.LENGTH_LONG).show();
            tvBmr.setText("BMR: Không hợp lệ");
            tvTdee.setText("TDEE: Không thể tính");
            spinnerActivityLevel.setEnabled(false); // Vô hiệu hóa Spinner
        } else {
            tvBmr.setText(String.format("BMR: %.0f kcal", bmr));

            // Thiết lập Spinner với layout tùy chỉnh
            String[] activityLevels = {
                    "Không vận động (BMR × 1.2)",
                    "Vận động nhẹ (BMR × 1.375)",
                    "Vận động vừa (BMR × 1.55)",
                    "Vận động cao (BMR × 1.725)",
                    "Vận động rất cao (BMR × 1.9)"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    activityLevels
            );
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dang);
            spinnerActivityLevel.setAdapter(adapter);

            // Xử lý khi chọn mức vận động
            spinnerActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    double factor;
                    switch (position) {
                        case 0: factor = 1.2; break;
                        case 1: factor = 1.375; break;
                        case 2: factor = 1.55; break;
                        case 3: factor = 1.725; break;
                        case 4: factor = 1.9; break;
                        default: factor = 1.2;
                    }
                    double tdee = bmr * factor;
                    tvTdee.setText(String.format("TDEE: %.0f kcal", tdee));
                    Toast.makeText(TDEEActivity.this, "Tính TDEE thành công!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    tvTdee.setText("TDEE: Chưa tính");
                }
            });
        }
    }
}