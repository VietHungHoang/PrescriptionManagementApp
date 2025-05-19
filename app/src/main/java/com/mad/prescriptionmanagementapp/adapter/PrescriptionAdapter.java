package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.Prescription;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private List<Prescription> prescriptionList;

    public PrescriptionAdapter(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);
        holder.tvMedicineName.setText(prescription.getDrugName());
        holder.tvScheduleStatus.setText(prescription.getSchedule());

        holder.btnEdit.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Chỉnh sửa " + prescription.getDrugName(), Toast.LENGTH_SHORT).show();
        });

        holder.tvViewInfo.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Xem thông tin " + prescription.getDrugName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvScheduleStatus, tvViewInfo;
        ImageView btnEdit;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvScheduleStatus = itemView.findViewById(R.id.tvSchedule);
            tvViewInfo = itemView.findViewById(R.id.tvViewInfo);
            btnEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}
