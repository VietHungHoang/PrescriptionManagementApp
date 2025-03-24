package com.mad.prescriptionmanagementapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.time.LocalDate;

public class CalendarFragment extends Fragment {
    private TextView tvMonthYear;
    private ViewPager2 viewPagerWeek;
    private WeekPagerAdapter weekPagerAdapter;
    private LocalDate today;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        viewPagerWeek = view.findViewById(R.id.viewPagerWeek);
        today = LocalDate.now();

        // Hiển thị tháng/năm hiện tại
        tvMonthYear.setText("Tháng " + today.getMonthValue() + " " + today.getYear());

        // Adapter tuần
        weekPagerAdapter = new WeekPagerAdapter(getContext(), today);
        viewPagerWeek.setAdapter(weekPagerAdapter);

        // Đặt tuần hiện tại vào giữa danh sách
        int middlePosition = weekPagerAdapter.getItemCount() / 2;
        viewPagerWeek.setCurrentItem(middlePosition, false);

        return view;
    }
}
