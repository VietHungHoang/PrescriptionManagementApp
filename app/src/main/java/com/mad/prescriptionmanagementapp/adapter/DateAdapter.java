package com.mad.prescriptionmanagementapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.BaseAdapter;

import com.mad.prescriptionmanagementapp.R;

import java.util.List;

public class DateAdapter extends BaseAdapter {

    private Context context;
    private List<String> daysOfWeek;
    private boolean[] selectedDays; // Lưu trạng thái của các CheckBox

    public DateAdapter(Context context, List<String> daysOfWeek) {
        this.context = context;
        this.daysOfWeek = daysOfWeek;
        selectedDays = new boolean[daysOfWeek.size()]; // Khởi tạo mảng lưu trạng thái
    }

    @Override
    public int getCount() {
        return daysOfWeek.size();
    }

    @Override
    public Object getItem(int position) {
        return daysOfWeek.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_date, parent, false);
        }

        // Lấy CheckBox và TextView
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        TextView dayText = convertView.findViewById(R.id.dayText);

        // Hiển thị tên ngày trong tuần (ví dụ: T2, T3, ...)
        String[] shortDays = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        dayText.setText(shortDays[position]);

        // Thiết lập trạng thái của CheckBox
        checkBox.setChecked(selectedDays[position]);

        // Đặt sự kiện click cho CheckBox để cập nhật trạng thái
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedDays[position] = isChecked;
        });

        return convertView;
    }

    // Hàm để lấy các ngày đã chọn
    public boolean[] getSelectedDays() {
        return selectedDays;
    }
}
