package com.mad.prescriptionmanagementapp.adapter.kiet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.PrescriptionResponse;

import java.util.List;

public class PrescriptionGroupAdapter extends RecyclerView.Adapter<PrescriptionGroupAdapter.GroupViewHolder> {

    private List<PrescriptionResponse> prescriptionList;
    private OnEditGroupClickListener editGroupClickListener;

    public PrescriptionGroupAdapter(List<PrescriptionResponse> prescriptionList, OnEditGroupClickListener listener) {
        this.prescriptionList = prescriptionList;
        this.editGroupClickListener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_drug_bill, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        PrescriptionResponse prescription = prescriptionList.get(position);
        holder.bind(prescription);
    }

    @Override
    public int getItemCount() {
        return prescriptionList != null ? prescriptionList.size() : 0;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle, btnEditGroup;
        RecyclerView recyclerMedicines;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            btnEditGroup = itemView.findViewById(R.id.btnEditGroup);
            recyclerMedicines = itemView.findViewById(R.id.recyclerMedicines);
        }

        public void bind(PrescriptionResponse prescription) {
            String name = prescription.getName() != null && !prescription.getName().isEmpty()
                    ? prescription.getName()
                    : "Nhóm thuốc";

            tvGroupTitle.setText(name + " (" + (prescription.getDrugs() != null ? prescription.getDrugs().size() : 0) + ")");

            btnEditGroup.setOnClickListener(v -> {
                if (editGroupClickListener != null) {
                    editGroupClickListener.onEditGroupClick(prescription);
                }
            });

            recyclerMedicines.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerMedicines.setAdapter(new DrugInPrescriptionAdapter(prescription.getDrugs()));
            recyclerMedicines.setNestedScrollingEnabled(false);
        }
    }

    public interface OnEditGroupClickListener {
        void onEditGroupClick(PrescriptionResponse prescription);
    }
}
