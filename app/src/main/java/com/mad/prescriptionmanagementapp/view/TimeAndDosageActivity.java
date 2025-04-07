package com.mad.prescriptionmanagementapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.prescriptionmanagementapp.R;

public class TimeAndDosageActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText edtDosage;
    private Button btnSaveTimeAndDosage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_and_dose); // Sử dụng layout cho Activity

        timePicker = findViewById(R.id.timePicker);
        edtDosage = findViewById(R.id.edtDosage);
        btnSaveTimeAndDosage = findViewById(R.id.btnSaveTimeAndDosage);

        btnSaveTimeAndDosage.setOnClickListener(v -> {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            String dosage = edtDosage.getText().toString();

            // Kiểm tra dữ liệu và gửi lại cho AddScheduleActivity
            if (dosage.isEmpty()) {
                edtDosage.setError("Vui lòng nhập liều");
                return;
            }

            // Gửi dữ liệu về AddScheduleActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedHour", hour);
            resultIntent.putExtra("selectedMinute", minute);
            resultIntent.putExtra("selectedDosage", dosage);
            setResult(RESULT_OK, resultIntent);
            finish();  // Đóng Activity và trả kết quả
        });
    }
}
