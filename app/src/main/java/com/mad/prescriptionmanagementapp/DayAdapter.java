package com.mad.prescriptionmanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    private List<String> days;
    private int selectedDay; // Ngày được chọn
    private int lastSelectedPosition = -1; // Vị trí ngày được chọn trước đó
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int dayNumber);
    }

    public DayAdapter(List<String> days, int selectedDay, OnItemClickListener listener) {
        this.days = days;
        this.selectedDay = selectedDay;
        this.listener = listener;

        // Tìm vị trí ngày được chọn ban đầu
        for (int i = 0; i < days.size(); i++) {
            if (Integer.parseInt(days.get(i).split(" ")[1]) == selectedDay) {
                lastSelectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(days.get(position), position);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDayOfWeek, tvDayNumber;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }

        public void bind(String day, int position) {
            Context context = itemView.getContext();
            String[] parts = day.split(" ");
            tvDayOfWeek.setText(parts[0]); // T2, T3, ...
            tvDayNumber.setText(parts[1]); // 25, 26, ...

            int dayNumber = Integer.parseInt(parts[1]);
            boolean isSelected = (position == lastSelectedPosition);

            // Cập nhật UI khi chọn ngày
            itemView.setBackgroundResource(isSelected ? R.drawable.bg_selected_day : android.R.color.transparent);
            int textColor = ContextCompat.getColor(context, isSelected ? android.R.color.white : android.R.color.black);
            tvDayOfWeek.setTextColor(textColor);
            tvDayNumber.setTextColor(textColor);

            // Bắt sự kiện click
            itemView.setOnClickListener(v -> {
                if (lastSelectedPosition != position) {
                    int previousPosition = lastSelectedPosition;
                    lastSelectedPosition = position;
                    selectedDay = dayNumber;

                    // Chỉ cập nhật lại 2 vị trí thay vì toàn bộ danh sách
                    if (previousPosition != -1) notifyItemChanged(previousPosition);
                    notifyItemChanged(position);

                    listener.onItemClick(dayNumber);
                }
            });
        }
    }

    // Cập nhật dữ liệu mới cho adapter
    public void updateDays(List<String> newDays, int selectedDay) {
        this.days = newDays;
        this.selectedDay = selectedDay;
        lastSelectedPosition = -1;

        for (int i = 0; i < newDays.size(); i++) {
            if (Integer.parseInt(newDays.get(i).split(" ")[1]) == selectedDay) {
                lastSelectedPosition = i;
                break;
            }
        }

        notifyDataSetChanged();
    }

    // Đặt ngày được chọn mới
    public void setSelectedDate(int day) {
        for (int i = 0; i < days.size(); i++) {
            if (Integer.parseInt(days.get(i).split(" ")[1]) == day) {
                int previousPosition = lastSelectedPosition;
                lastSelectedPosition = i;
                selectedDay = day;

                if (previousPosition != -1) notifyItemChanged(previousPosition);
                notifyItemChanged(i);
                break;
            }
        }
    }
}
