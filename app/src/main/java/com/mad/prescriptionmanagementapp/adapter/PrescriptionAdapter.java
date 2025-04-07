package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.Prescription;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private List<Prescription> prescriptionList;

    public PrescriptionAdapter(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    @Override
    public PrescriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);
        holder.tvDrug.setText(prescription.getDrugName());
        holder.tvSchedule.setText(prescription.getSchedule());

    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public class PrescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView tvDrug, tvSchedule, tvMedicines;

        public PrescriptionViewHolder(View itemView) {
            super(itemView);
            tvDrug = itemView.findViewById(R.id.tvDrug);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvMedicines = itemView.findViewById(R.id.tvMedicines);
        }
    }
}
