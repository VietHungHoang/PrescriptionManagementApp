package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.BaseItem;
import com.mad.prescriptionmanagementapp.model.Prescription;
import com.mad.prescriptionmanagementapp.model.PrescriptionGroup;

import java.util.List;

public class MixedPrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BaseItem> items;

    public MixedPrescriptionAdapter(List<BaseItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BaseItem.TYPE_PRESCRIPTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_prescription, parent, false);
            return new PrescriptionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_drug_bill, parent, false);
            return new GroupViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseItem item = items.get(position);
        if (holder instanceof PrescriptionViewHolder) {
            Prescription p = (Prescription) item;
            ((PrescriptionViewHolder) holder).bind(p);
        } else {
            PrescriptionGroup group = (PrescriptionGroup) item;
            ((GroupViewHolder) holder).bind(group);
        }
    }

    static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvTime = itemView.findViewById(R.id.tvSchedule);
        }

        public void bind(Prescription p) {
            tvName.setText(p.getDrugName());
            tvTime.setText(p.getDrugName());
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle, btnEditGroup;
        RecyclerView recyclerMedicines;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            btnEditGroup = itemView.findViewById(R.id.btnEditGroup);
            recyclerMedicines = itemView.findViewById(R.id.recyclerMedicines);
        }

        public void bind(PrescriptionGroup group) {
            tvGroupTitle.setText(group.getGroupName() + " (" + group.getMedicineList().size() + ")");
            recyclerMedicines.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerMedicines.setAdapter(new PrescriptionAdapter(group.getMedicineList()));
        }
    }
}
