package com.mad.prescriptionmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.databinding.ViewholderDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;

import java.util.ArrayList;
import java.util.List;

public class DrugsAdapter extends RecyclerView.Adapter<DrugsAdapter.DrugViewHolder> {
    private final OnDrugClickListener listener;

    private List<SimpleDrug> drugs;
    private List<SimpleDrug> filteredList;

    public DrugsAdapter(List<SimpleDrug> drugs, OnDrugClickListener listener) {
//        super(DIFF_CALLBACK);
        this.drugs = drugs;
        this.filteredList = drugs;
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
        holder.bind(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void updateData(List<SimpleDrug> newList) {
        this.drugs = newList;
        this.filteredList = new ArrayList<>(drugs);
        notifyDataSetChanged(); // hoặc notifyItemRangeChanged(...) cho tối ưu hơn
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(drugs);
        } else {
            for (SimpleDrug item : drugs) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class DrugViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderDrugBinding binding;
        private final OnDrugClickListener clickListener;

        public DrugViewHolder(ViewholderDrugBinding binding, OnDrugClickListener clickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = clickListener;
        }

        public void bind(SimpleDrug drug) {
            this.binding.drugName.setText(drug.getName());
            this.binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(drug);
                }
            });
        }
    }
}
