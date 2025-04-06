package com.mad.prescriptionmanagementapp.ui.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mad.prescriptionmanagementapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private PieChart pieChart;
    private TextView startDate;
    private TextView endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Ánh xạ UI
        spinner = findViewById(R.id.spinner_prescription);
        pieChart = findViewById(R.id.pieChart);
        startDate = findViewById(R.id.StartDate);
        endDate = findViewById(R.id.Enddate);
        setupSpinner();
        setupPieChart();


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DatePicker cho ngày bắt đầu
                showDatePickerDialog(startDate, true);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DatePicker cho ngày kết thúc
                showDatePickerDialog(endDate, false);
            }
        });
    }

    private void setupSpinner() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Chọn thuốc...");
        list.add("Thuốc ho");
        list.add("Thuốc đau đầu");
        list.add("Thuốc sổ mũi");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Nếu chọn thuốc (không phải "Chọn thuốc...")
                    String selectedItem = list.get(position);
                    Toast.makeText(MainActivity.this, "Đã chọn: " + selectedItem, Toast.LENGTH_SHORT).show();
                    loadPieChartData(selectedItem); // Cập nhật biểu đồ
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHighlightPerTapEnabled(false); // Tắt highlight để tránh cache dữ liệu cũ
    }

    private void loadPieChartData(String medicine) {
        List<PieEntry> entries = new ArrayList<>();

        switch (medicine) {
            case "Thuốc ho":
                entries.add(new PieEntry(40f, "Đúng giờ"));
                entries.add(new PieEntry(30f, "Muộn"));
                entries.add(new PieEntry(30f, "Không uống"));
                break;
            case "Thuốc đau đầu":
                entries.add(new PieEntry(50f, "Đúng giờ"));
                entries.add(new PieEntry(30f, "Muộn"));
                entries.add(new PieEntry(20f, "Không uống"));
                break;
            case "Thuốc sổ mũi":
                entries.add(new PieEntry(60f, "Đúng giờ"));
                entries.add(new PieEntry(20f, "Muộn"));
                entries.add(new PieEntry(20f, "Không uống"));
                break;
            default:
                entries.add(new PieEntry(100f, "Không có dữ liệu"));
        }

        // Xóa dữ liệu cũ trước khi cập nhật
        pieChart.clear();

        // Màu sắc
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#28A745"));
        colors.add(Color.parseColor("#ED791D"));
        colors.add(Color.parseColor("#FF4D4D"));

        PieDataSet dataSet = new PieDataSet(entries, "Tình trạng uống thuốc");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.notifyDataSetChanged();  // Cập nhật dữ liệu mới
        pieChart.invalidate(); // Vẽ lại biểu đồ
    }
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();

    private void showDatePickerDialog(final TextView targetTextView, boolean isStartDate) {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                (view, year1, month1, dayOfMonth1) -> {
                    calendar.set(year1, month1, dayOfMonth1);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi"));
                    String selectedDate = sdf.format(calendar.getTime());

                    if (isStartDate) {
                        // Nếu ngày bắt đầu > ngày kết thúc, hiển thị lỗi
                        if (endDateCalendar.getTimeInMillis() > 0 && calendar.after(endDateCalendar)) {
                            Toast.makeText(MainActivity.this, "Ngày bắt đầu không thể sau ngày kết thúc!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startDateCalendar.setTime(calendar.getTime());
                    } else {
                        // Nếu ngày kết thúc < ngày bắt đầu, hiển thị lỗi
                        if (calendar.before(startDateCalendar)) {
                            Toast.makeText(MainActivity.this, "Ngày kết thúc không thể trước ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        endDateCalendar.setTime(calendar.getTime());
                    }

                    targetTextView.setText(selectedDate);
                }, year, month, dayOfMonth);

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

}
