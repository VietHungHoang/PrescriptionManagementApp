package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.databinding.ViewholderDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnItemClickListener;

import java.util.List;

public class DrugsAdapter extends RecyclerView.Adapter<DrugsAdapter.DrugViewHolder> {
    private final OnItemClickListener<DrugResponse> listener;

    private final List<DrugResponse> drugs;

    public DrugsAdapter(List<DrugResponse> drugs, OnItemClickListener<DrugResponse> listener) {
//        super(DIFF_CALLBACK);
        this.drugs = drugs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderDrugBinding binding = ViewholderDrugBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DrugViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        holder.bind(drugs.get(position));
    }

    @Override
    public int getItemCount() {
        return drugs.size();
    }

    static class DrugViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderDrugBinding binding;
        private final OnItemClickListener<DrugResponse> clickListener;

        public DrugViewHolder(ViewholderDrugBinding binding, OnItemClickListener<DrugResponse> clickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = clickListener;
        }

        public void bind(DrugResponse drug) {
            this.binding.drugName.setText(drug.getName());
            this.binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(drug);
                }
            });
        }
    }
}
