package com.mad.prescriptionmanagementapp.adapter.kiet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.kiet.DayModel;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    private List<DayModel> days;
    private int selectedDay, selectedMonth;

    private int lastSelectedPosition = -1;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int dayNumber);
    }

    public DayAdapter(List<DayModel> days, int selectedDay,int selectedMonth, OnItemClickListener listener) {

        this.listener = listener;
        this.days = days;
        this.selectedDay = selectedDay;
        this.selectedMonth = selectedMonth;


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

            // Kiểm tra nếu ID bị null -> báo lỗi
            if (tvDayOfWeek == null || tvDayNumber == null) {
                throw new RuntimeException("Lỗi: Không tìm thấy tvDayOfWeek hoặc tvDayNumber trong item_day.xml");
            }
        }

        public void bind(DayModel day, int position) {
            Context context = itemView.getContext();
            tvDayOfWeek.setText(day.getDayOfWeek());
            tvDayNumber.setText(String.valueOf(day.getDayNumber()));

            // Chỉ bôi nếu cả ngày và tháng đều trùng
            boolean isSelected = (day.getDayNumber() == selectedDay && day.getMonth() == selectedMonth);

            itemView.setBackgroundResource(isSelected ? R.drawable.bg_selected_day : android.R.color.transparent);
            int textColor = ContextCompat.getColor(context, isSelected ? android.R.color.white : android.R.color.black);
            tvDayOfWeek.setTextColor(textColor);
            tvDayNumber.setTextColor(textColor);

            itemView.setOnClickListener(v -> {
                int previousPosition = getAdapterPosition();
                selectedDay = day.getDayNumber();
                selectedMonth = day.getMonth();

                notifyDataSetChanged(); // hoặc gọi notifyItemChanged(previous/new) nếu cần hiệu năng
                listener.onItemClick(day.getDayNumber());
            });
        }
    }

    public void updateDays(List<DayModel> newDays, int selectedDay, int selectedMonth) {
        this.days = newDays;
        this.selectedDay = selectedDay;
        lastSelectedPosition = -1;


        notifyDataSetChanged();
    }

    public void setSelectedDate(int day, int month) {
        this.selectedDay = day;
        this.selectedMonth = month;
        notifyDataSetChanged();
    }
}
