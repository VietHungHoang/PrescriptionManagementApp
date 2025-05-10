package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.R;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Drug> drugList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Drug drug);
    }

    public MedicineAdapter(List<Drug> drugList, OnItemClickListener listener) {
        this.drugList = drugList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Drug drug = drugList.get(position);
        holder.drugNameText.setText(drug.getName() != null ? drug.getName() : "Không có tên");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(drug);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drugList != null ? drugList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView drugNameText;

        public ViewHolder(View itemView) {
            super(itemView);
            drugNameText = itemView.findViewById(R.id.drug_name);
        }
    }
}