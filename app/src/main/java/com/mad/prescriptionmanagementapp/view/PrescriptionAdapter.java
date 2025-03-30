package com.mad.prescriptionmanagementapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.Prescription;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {
    private List<Prescription> prescriptionList;
    private Context context;

    public PrescriptionAdapter(Context context, List<Prescription> prescriptionList) {
        this.context = context;
        this.prescriptionList = prescriptionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);
        holder.tvTitle.setText(prescription.getTitle());

        // Xử lý danh sách thuốc
        String medicines = prescription.getMedicines();
        String[] medicineArray = medicines.split("\n");

        if (medicineArray.length > 2) {
            // Hiển thị 2 loại đầu và thêm số lượng thuốc còn lại
            String displayText = medicineArray[0] + "\n" + medicineArray[1] + "\n+" + (medicineArray.length - 2) + " loại thuốc khác";
            holder.tvMedicines.setText(displayText);
        } else {
            // Hiển thị bình thường nếu có 2 hoặc ít hơn
            holder.tvMedicines.setText(medicines);
        }

        holder.ivEdit.setOnClickListener(v -> {
            // Xử lý sự kiện chỉnh sửa đơn thuốc
            Toast.makeText(context, "Chỉnh sửa: " + prescription.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMedicines;
        ImageView ivEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMedicines = itemView.findViewById(R.id.tvMedicines);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}
