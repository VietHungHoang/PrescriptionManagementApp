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
import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history_item_kiet, parent, false);
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
        List<MedicineItem> medicineItems = new ArrayList<>();

        for (MedicineResponse.TimeDosage dosage : timeDosages) {
            // Tạo MedicineItem mới cho mỗi TimeDosage
            MedicineItem item = new MedicineItem();
            item.setTime(dosage.getTime());
            item.setDate(date);

            // Tạo danh sách thuốc dạng chuỗi, mỗi thuốc trên 1 dòng
            StringBuilder medicineListBuilder = new StringBuilder();
            for (MedicineResponse.Drug drug : dosage.getDrugs()) {
                medicineListBuilder.append(drug.getName())
                        .append(" - ")
                        .append(drug.getDosage())
                        .append(drug.getUnit())
                        .append("\n");
            }
            item.setMedicineList(medicineListBuilder.toString().trim());

            // Cài đặt trạng thái dùng thuốc
            if (dosage.isEditted()) {
                if (dosage.getStatus() == 2) { // Dùng muộn
                    item.setUsedLate(false);
                    item.setUsed(true);
                    item.setSkipped(false);
                } else if (dosage.getStatus() == 1) { // Đã dùng bình thường
                    item.setUsed(false);
                    item.setSkipped(false);
                    item.setUsedLate(true);
                } else if (dosage.getStatus() == 0) { // Bỏ qua
                    item.setUsed(false);
                    item.setSkipped(true);
                    item.setUsedLate(false);
                } else {
                    item.setUsed(false);
                    item.setSkipped(false);
                    item.setUsedLate(false);
                }
            } else {
                item.setUsed(false);
                item.setSkipped(false);
                item.setUsedLate(false);
            }

            medicineItems.add(item);
        }

        return medicineItems;
    }

}

