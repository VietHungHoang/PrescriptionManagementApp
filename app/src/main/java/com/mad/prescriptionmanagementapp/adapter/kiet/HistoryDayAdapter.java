package com.mad.prescriptionmanagementapp.adapter.kiet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.kiet.MedicineItem;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.MedicineResponse;
import com.mad.prescriptionmanagementapp.ui.listener.kiet.OnMedicineActionListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryDayAdapter extends RecyclerView.Adapter<HistoryDayAdapter.DayViewHolder> {

    private List<MedicineResponse> daysList = new ArrayList<>();
    private OnMedicineActionListener listener;

    private final List<MedicineItem> flatMedicineItems = new ArrayList<>();

    public HistoryDayAdapter(List<MedicineResponse> daysList, OnMedicineActionListener listener) {
        this.listener = listener;
        updateData(daysList);
    }

    public void updateData(List<MedicineResponse> newDaysList) {
        this.daysList = newDaysList;
        flattenMedicineItems();
        notifyDataSetChanged();
    }

    private void flattenMedicineItems() {
        flatMedicineItems.clear();
        for (MedicineResponse day : daysList) {
            flatMedicineItems.addAll(convertToMedicineItems(day.getTimeDosages(), day.getDate()));
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        MedicineResponse dayData = daysList.get(position);
        holder.tvDate.setText("Ngày " + dayData.getDate());

        List<MedicineItem> medicineItems = convertToMedicineItems(dayData.getTimeDosages(), dayData.getDate());

        holder.rvMedicines.setLayoutManager(new LinearLayoutManager(holder.rvMedicines.getContext(), LinearLayoutManager.VERTICAL, false));
        holder.rvMedicines.setNestedScrollingEnabled(false); // Quan trọng để RecyclerView con không bị cuộn riêng biệt
        MedicineAdapter medicineAdapter = new MedicineAdapter(medicineItems, listener);
        holder.rvMedicines.setAdapter(medicineAdapter);
    }


    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public MedicineItem getMedicineItemByPosition(int globalPosition) {
        if (flatMedicineItems.isEmpty()) {
            flattenMedicineItems();
        }
        if (globalPosition >= 0 && globalPosition < flatMedicineItems.size()) {
            return flatMedicineItems.get(globalPosition);
        }
        return null;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView rvMedicines;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            rvMedicines = itemView.findViewById(R.id.recyclerView);
        }
    }

    private List<MedicineItem> convertToMedicineItems(List<MedicineResponse.TimeDosage> timeDosages, String date) {
        Map<String, MedicineItem> groupedByTime = new LinkedHashMap<>();

        for (MedicineResponse.TimeDosage dosage : timeDosages) {
            String time = dosage.getTime();
            MedicineItem item = groupedByTime.get(time);

            // Nếu chưa có time này, tạo mới
            if (item == null) {
                item = new MedicineItem();
                item.setTime(time);
                item.setDate(date);
                item.setMedicineList("");
                item.setUsed(false);
                item.setSkipped(false);
                groupedByTime.put(time, item);
            }

            // Thêm thuốc vào danh sách, cách nhau xuống dòng
            String existingList = item.getMedicineList();
            StringBuilder sb = new StringBuilder(existingList.isEmpty() ? "" : existingList + "\n");
            for (MedicineResponse.Drug drug : dosage.getDrugs()) {
                sb.append(drug.getName())
                        .append(" - ")
                        .append(drug.getDosage())
                        .append(drug.getUnit());
            }
            item.setMedicineList(sb.toString());

            if (dosage.isEditted()) {
                if (dosage.getStatus() == 2) { // Dùng muộn
                    item.setUsedLate(true);
                    item.setUsed(false);
                    item.setSkipped(false);
                } else if (dosage.getStatus() == 1) { // Đã dùng bình thường
                    item.setUsed(true);
                    item.setSkipped(false);
                    item.setUsedLate(false);
                } else if (dosage.getStatus() == 0) { // Bỏ qua
                    item.setUsed(false);
                    item.setSkipped(true);
                    item.setUsedLate(false);
                } else {
                    // Các trạng thái khác hoặc mặc định
                    item.setUsed(false);
                    item.setSkipped(false);
                    item.setUsedLate(false);
                }
            }

        }
        return new ArrayList<>(groupedByTime.values());
    }

}

