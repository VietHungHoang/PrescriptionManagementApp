package com.mad.prescriptionmanagementapp.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.view.FrequencySelectionActivity;
import com.mad.prescriptionmanagementapp.view.TimeAndDosageActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText edtDosage, edtStartDate;
    private AutoCompleteTextView edtFrequency;
    private Button btnAddTimeAndDosage;

    private LinearLayout linearLayoutItems;
    private ArrayList<String> timeAndDosageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        edtStartDate = findViewById(R.id.edtStartDate);
        edtFrequency = findViewById(R.id.spinnerFrequency);
        btnAddTimeAndDosage = findViewById(R.id.btn_them_gio);
        linearLayoutItems = findViewById(R.id.linearLayoutItems);


        AutoCompleteTextView spinnerUnit = findViewById(R.id.spinnerUnit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.unit_array,
                android.R.layout.simple_dropdown_item_1line
        );
        spinnerUnit.setAdapter(adapter);

        spinnerUnit.setOnTouchListener((v, event) -> {
            if (spinnerUnit.getDropDownHeight() > 0) {
                spinnerUnit.dismissDropDown();
            } else {
                spinnerUnit.showDropDown();
            }
            return true;
        });

        setStartDate();
        setFrequencySelection();
        setAddTimeAndDosage();
    }

    private void setStartDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        edtStartDate.setText(String.format("%02d/%02d/%d", day, month + 1, year));

        edtStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddScheduleActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        edtStartDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setFrequencySelection() {
        edtFrequency.setOnClickListener(v -> {
            Intent intent = new Intent(AddScheduleActivity.this, FrequencySelectionActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void setAddTimeAndDosage() {
        btnAddTimeAndDosage.setOnClickListener(v -> {
            Intent intent = new Intent(AddScheduleActivity.this, TimeAndDosageActivity.class);
            startActivityForResult(intent, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra xem dữ liệu có hợp lệ không
        if (data == null) {
            Log.e("onActivityResult", "Intent data is null.");
            return; // Nếu dữ liệu trả về là null, không thực hiện gì thêm
        }

        // Xử lý khi requestCode là 1 (Frequency)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String selectedFrequency = data.getStringExtra("selectedFrequency");

            // Kiểm tra nếu dữ liệu không null trước khi cập nhật vào EditText
            if (selectedFrequency != null) {
                edtFrequency.setText(selectedFrequency);
            } else {
                Log.e("onActivityResult", "selectedFrequency is null.");
            }
        }

        // Xử lý khi requestCode là 2 (Time and Dosage)
        if (requestCode == 2 && resultCode == RESULT_OK) {
            // Kiểm tra và lấy giá trị từ Intent, đảm bảo chúng không null
            int hour = data.getIntExtra("selectedHour", -1); // -1 là giá trị mặc định nếu không có dữ liệu
            int minute = data.getIntExtra("selectedMinute", -1); // -1 là giá trị mặc định nếu không có dữ liệu
            String dosage = data.getStringExtra("selectedDosage");

            // Kiểm tra dữ liệu hợp lệ
            if (hour == -1 || minute == -1 || dosage == null) {
                Log.e("onActivityResult", "Invalid data received. hour: " + hour + ", minute: " + minute + ", dosage: " + dosage);
                return; // Nếu dữ liệu không hợp lệ, không làm gì thêm
            }

            // Tạo chuỗi thông tin về giờ và liều
            String timeAndDosage = String.format("Giờ: %02d:%02d, Liều: %s", hour, minute, dosage);

            // Thêm chuỗi vào danh sách nếu không rỗng
            if (timeAndDosageList == null) {
                timeAndDosageList = new ArrayList<>(); // Khởi tạo danh sách nếu chưa được khởi tạo
            }

            timeAndDosageList.add(timeAndDosage);

            // Gọi hàm cập nhật danh sách sau khi thêm item mới
            updateTimeAndDosageList();
        }
    }


    private void updateTimeAndDosageList() {
        linearLayoutItems.removeAllViews();

        for (int i = 0; i < timeAndDosageList.size(); i++) {
            String item = timeAndDosageList.get(i);

            // Inflate từ layout XML
            View itemView = getLayoutInflater().inflate(R.layout.item_time_and_dosage, null);

            TextView tvItemText = itemView.findViewById(R.id.tvItemText);
            ImageButton btnDelete = itemView.findViewById(R.id.btnDeleteItem);

            tvItemText.setText(item);

            final int index = i;
            btnDelete.setOnClickListener(v -> {
                timeAndDosageList.remove(index);
                updateTimeAndDosageList();
            });

            linearLayoutItems.addView(itemView);
        }
    }




}



