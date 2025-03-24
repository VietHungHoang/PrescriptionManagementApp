package com.mad.prescriptionmanagementapp;

import static android.os.Build.VERSION_CODES.R;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeekPagerAdapter extends RecyclerView.Adapter<WeekPagerAdapter.WeekViewHolder> {

    private final Context context;
    private final LocalDate today;

    public WeekPagerAdapter(Context context, LocalDate today) {
        this.context = context;
        this.today = today;
    }

    @Override
    public WeekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_week, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekViewHolder holder, int position) {
        // Xác định ngày bắt đầu của tuần hiện tại (tính từ thứ 2)
        int middlePosition = getItemCount() / 2;
        LocalDate startOfWeek = today.plusWeeks(position - middlePosition).with(DayOfWeek.MONDAY);

        // Clear View cũ để tránh đè lên
        holder.layoutDaysOfWeek.removeAllViews();
        holder.layoutDates.removeAllViews();

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);

            // Tạo view cho thứ và ngày
            TextView tvDay = createTextView(context, getDayName(i), false);
            TextView tvDate = createTextView(context, dayFormatter.format(date), true);

            // Nếu là ngày hôm nay -> bôi nền xanh, chữ trắng
            if (date.equals(today)) {
                tvDay.setBackgroundResource(R.drawable.bg_selected_date);
                tvDate.setBackgroundResource(R.drawable.bg_selected_date);
                tvDay.setTextColor(Color.WHITE);
                tvDate.setTextColor(Color.WHITE);
            }

            holder.layoutDaysOfWeek.addView(tvDay);
            holder.layoutDates.addView(tvDate);
        }
    }

    @Override
    public int getItemCount() {
        return 100;  // Tuần: 50 trước + 50 sau
    }

    private String getDayName(int index) {
        switch (index) {
            case 0: return "T2";
            case 1: return "T3";
            case 2: return "T4";
            case 3: return "T5";
            case 4: return "T6";
            case 5: return "T7";
            default: return "CN";
        }
    }

    private TextView createTextView(Context ctx, String text, boolean isDate) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, ctx.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(4, 0, 4, 0);
        tv.setLayoutParams(params);
        tv.setTextColor(Color.BLACK);
        return tv;
    }

    static class WeekViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutDaysOfWeek, layoutDates;

        WeekViewHolder(View itemView) {
            super(itemView);
            layoutDaysOfWeek = itemView.findViewById(R.id.layoutDaysOfWeek);
            layoutDates = itemView.findViewById(R.id.layoutDates);
        }
    }
}
