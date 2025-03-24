package com.mad.prescriptionmanagementapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<DayItem> dayList;
    private int selectedPosition = -1;

    public DayAdapter(List<DayItem> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayItem item = dayList.get(position);
        holder.tvWeekday.setText(item.weekday);
        holder.tvDate.setText(item.date);

        if (position == selectedPosition) {
            holder.tvWeekday.setTextColor(Color.parseColor("#2196F3"));
            holder.tvDate.setBackgroundResource(R.drawable.bg_circle_blue);
            holder.tvDate.setTextColor(Color.WHITE);
        } else {
            holder.tvWeekday.setTextColor(Color.BLACK);
            holder.tvDate.setBackgroundResource(R.drawable.bg_circle_gray);
            holder.tvDate.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeekday, tvDate;
        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeekday = itemView.findViewById(R.id.tvWeekday);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
