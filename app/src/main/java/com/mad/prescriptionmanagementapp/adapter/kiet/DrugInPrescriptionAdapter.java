package com.mad.prescriptionmanagementapp.adapter.kiet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.DrugResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.ScheduleResponse;

import java.util.List;

public class DrugInPrescriptionAdapter extends RecyclerView.Adapter<DrugInPrescriptionAdapter.DrugViewHolder> {

    private List<DrugResponse> drugList;
    private OnEditClickListener editClickListener;

    public DrugInPrescriptionAdapter(List<DrugResponse> drugList) {
        this.drugList = drugList;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_prescription_kiet, parent, false);
        return new DrugViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        holder.bind(drugList.get(position));
    }

    @Override
    public int getItemCount() {
        return drugList != null ? drugList.size() : 0;
    }

    class DrugViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicineName, tvSchedule, tvViewInfo;

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvViewInfo = itemView.findViewById(R.id.tvViewInfo);
        }

        public void bind(DrugResponse drug) {
            tvMedicineName.setText(drug.getDrugName());

            String scheduleStr = "Chưa có giờ uống";
            List<ScheduleResponse> schedules = drug.getSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                // Tìm giờ uống sớm nhất
                String earliestTime = schedules.stream()
                        .map(ScheduleResponse::getDate)
                        .filter(dateStr -> dateStr != null && !dateStr.isEmpty())
                        .min(String::compareTo)  // So sánh theo chuỗi ISO, giờ nhỏ nhất sẽ đứng đầu
                        .orElse("");

                if (!earliestTime.isEmpty()) {
                    String formattedTime = formatDateTimeString(earliestTime);
                    scheduleStr = "Giờ uống gần nhất: " + formattedTime;
                }
            }

            tvSchedule.setText(scheduleStr);

            tvViewInfo.setText("Xem thông tin chi tiết thuốc");

            // Xử lý click xem chi tiết nếu cần
        }

        private String formatDateTimeString(String isoDateTime) {
            if (isoDateTime == null || isoDateTime.isEmpty()) {
                return "";
            }
            try {
                int tIndex = isoDateTime.indexOf('T');
                if (tIndex == -1 || tIndex + 6 > isoDateTime.length()) {
                    return isoDateTime;
                }
                return isoDateTime.substring(tIndex + 1, tIndex + 6);
            } catch (Exception e) {
                return isoDateTime;
            }
        }
    }

    public interface OnEditClickListener {
        void onEditClick(DrugResponse drug);
    }
}
