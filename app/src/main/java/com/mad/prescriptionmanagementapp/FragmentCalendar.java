package com.mad.prescriptionmanagementapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FragmentCalendar extends Fragment {
    private RecyclerView recyclerViewDays;
    private TextView tvMonthYear;
    private ImageView btnPrevWeek, btnNextWeek;
    private Calendar calendar;
    private DayAdapter adapter;
    private List<String> weekDays = new ArrayList<>();
    private final String[] weekLabels = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"}; // Thứ tự từ CN → T7
    private int selectedDay; // Lưu ngày đang bôi xanh

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        recyclerViewDays = view.findViewById(R.id.recyclerViewDays);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);

        calendar = Calendar.getInstance(); // Lấy ngày hiện tại
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH); // Ngày mặc định bôi xanh

        updateMonthYearText();
        updateWeekDays();

        tvMonthYear.setOnClickListener(v -> showMonthYearPickerDialog());

        recyclerViewDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new DayAdapter(weekDays, selectedDay, position -> {
            // Khi chọn ngày trên giao diện
            selectedDay = Integer.parseInt(weekDays.get(position).split(" ")[1]);
            adapter.setSelectedDate(selectedDay);
        });
        recyclerViewDays.setAdapter(adapter);

        // Xử lý sự kiện bấm nút lùi tuần
        btnPrevWeek.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            updateMonthYearText();
            updateWeekDays();
        });

        // Xử lý sự kiện bấm nút tiến tuần
        btnNextWeek.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            updateMonthYearText();
            updateWeekDays();
        });

        return view;
    }

    private void updateMonthYearText() {
        String monthYear = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN")).format(calendar.getTime());
        tvMonthYear.setText(monthYear);
    }

    private void updateWeekDays() {
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
        weekDays.clear();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Bắt đầu từ CN

        for (int i = 0; i < 7; i++) {
            String day = sdfDay.format(tempCalendar.getTime());
            weekDays.add(weekLabels[i] + " " + day); // VD: "CN 24"
            tempCalendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        if (adapter != null) {
            adapter.updateDays(weekDays, selectedDay);
        }
    }

    private void showMonthYearPickerDialog() {
        if (getActivity() == null) return;

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = selectedDay; // Hiển thị ngày bôi xanh hiện tại

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    selectedDay = selectedDayOfMonth;

                    updateMonthYearText();
                    updateWeekDays();

                    if (adapter != null) {
                        adapter.setSelectedDate(selectedDay);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
