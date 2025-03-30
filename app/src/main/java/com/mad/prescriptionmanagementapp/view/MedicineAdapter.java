package com.mad.prescriptionmanagementapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.model.MedicineItem;
import com.mad.prescriptionmanagementapp.R;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private List<MedicineItem> medicineList;
    private OnMedicineActionListener listener;

    public MedicineAdapter(List<MedicineItem> medicineList, OnMedicineActionListener listener) {
        this.medicineList = medicineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_medicine_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicineItem item = medicineList.get(position);
        holder.tvTime.setText(item.getTime());

        // Hiển thị danh sách thuốc (tối đa 3 dòng)
        String[] medicines = item.getMedicineList().split("\n");
        int maxItems = 3;
        String displayText = medicines.length > maxItems
                ? String.join("\n", java.util.Arrays.copyOfRange(medicines, 0, maxItems))
                : item.getMedicineList();
        holder.tvMedicineList.setText(displayText);

        // Sự kiện nhấn vào "Xem chi tiết"
        holder.tvViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClicked(position);
            }
        });

        // Xử lý nút "Dùng"
        holder.btnUsed.setOnClickListener(v -> {
            if (listener != null) {
                item.setUsed(true);
                item.setSkipped(false);
                notifyItemChanged(position);
                listener.onUsedClicked(position);
            }
        });

        // Xử lý nút "Bỏ Qua"
        holder.btnSkip.setOnClickListener(v -> {
            if (listener != null) {
                item.setSkipped(true);
                item.setUsed(false);
                notifyItemChanged(position);
                listener.onSkippedClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    // Hàm cập nhật danh sách thuốc mới
    public void updateList(List<MedicineItem> newList) {
        this.medicineList = newList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvMedicineList, tvUsed, tvSkip, tvViewDetails;
        LinearLayout btnUsed, btnSkip;
        ImageView imgUsed, imgSkip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMedicineList = itemView.findViewById(R.id.tvMedicineList);
            tvViewDetails = itemView.findViewById(R.id.tvDetail);
            btnUsed = itemView.findViewById(R.id.btnUsed);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            imgUsed = itemView.findViewById(R.id.imgUsed);
            imgSkip = itemView.findViewById(R.id.imgSkip);
            tvUsed = itemView.findViewById(R.id.tvUsed);
            tvSkip = itemView.findViewById(R.id.tvSkip);
        }
    }
}
