package com.mad.prescriptionmanagementapp.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.R;

public class FrequencySelectionActivity extends AppCompatActivity {

    RadioGroup radioGroupFrequency;
    LinearLayout numberOfDaysLayout, daysOfWeekLayout;
    EditText edtNumberOfDays;
    Button  btnSelect;
    ImageButton btnDecrease, btnIncrease;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency_selection);

        radioGroupFrequency = findViewById(R.id.radioGroupFrequency);
        numberOfDaysLayout = findViewById(R.id.numberOfDaysLayout);
        daysOfWeekLayout = findViewById(R.id.daysOfWeekLayout);
        edtNumberOfDays = findViewById(R.id.edtNumberOfDays);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnSelect = findViewById(R.id.btnSelect);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // Mặc định chọn "Hằng ngày"
        radioGroupFrequency.check(R.id.radioDaily);
        numberOfDaysLayout.setVisibility(View.GONE);
        daysOfWeekLayout.setVisibility(View.GONE);

        radioGroupFrequency.setOnCheckedChangeListener((group, checkedId) -> {
            numberOfDaysLayout.setVisibility(checkedId == R.id.radioEveryNDays ? View.VISIBLE : View.GONE);
            daysOfWeekLayout.setVisibility(checkedId == R.id.radioSpecificDays ? View.VISIBLE : View.GONE);
        });

        // Nút giảm
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(edtNumberOfDays.getText().toString());
                if (currentValue > 1) { // Đảm bảo không giảm dưới 1
                    edtNumberOfDays.setText(String.valueOf(currentValue - 1));
                }
            }
        });

// Nút tăng
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(edtNumberOfDays.getText().toString());
                edtNumberOfDays.setText(String.valueOf(currentValue + 1));
            }
        });

        btnSelect.setOnClickListener(v -> {
            String result = "";
            int checkedId = radioGroupFrequency.getCheckedRadioButtonId();

            if (checkedId == R.id.radioDaily) {
                result = "Uống hằng ngày";
            } else if (checkedId == R.id.radioEveryNDays) {
                result = "Uống cách " + edtNumberOfDays.getText().toString() + " ngày";
            } else {
                result = "Uống vào các ngày: ";
                result += getSelectedDays();
            }

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        });
    }

    private String getSelectedDays() {
        StringBuilder days = new StringBuilder();
        int[] ids = {R.id.cbMonday, R.id.cbTuesday, R.id.cbWednesday, R.id.cbThursday, R.id.cbFriday, R.id.cbSaturday, R.id.cbSunday};
        String[] labels = {"Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy", "Chủ nhật"};

        for (int i = 0; i < ids.length; i++) {
            CheckBox cb = findViewById(ids[i]);
            if (cb.isChecked()) {
                days.append(labels[i]).append(", ");
            }
        }

        if (days.length() > 0) {
            days.setLength(days.length() - 2); // remove last comma
        } else {
            days.append("Không có ngày nào được chọn");
        }

        return days.toString();
    }
}
