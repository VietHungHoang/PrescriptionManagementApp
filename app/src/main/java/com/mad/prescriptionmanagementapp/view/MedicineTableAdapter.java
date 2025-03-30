package com.mad.prescriptionmanagementapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.MedicineTableItem;

import java.util.List;

public class MedicineTableAdapter extends RecyclerView.Adapter<MedicineTableAdapter.ViewHolder> {
    private final List<MedicineTableItem> medicineList;

    public MedicineTableAdapter(List<MedicineTableItem> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicineTableItem medicine = medicineList.get(position);
        holder.tvMedicineName.setText(medicine.getName());
        holder.tvQuantity.setText(medicine.getQuantity());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
