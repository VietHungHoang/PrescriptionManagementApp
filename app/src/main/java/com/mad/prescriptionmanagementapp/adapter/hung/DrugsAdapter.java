package com.mad.prescriptionmanagementapp.adapter.hung;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.databinding.ViewholderDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.util.ArrayList;
import java.util.List;

public class DrugsAdapter extends RecyclerView.Adapter<DrugsAdapter.DrugViewHolder> {
    private final OnDrugClickListener listener;

    private List<SimpleDrug> drugs;
    private List<SimpleDrug> filteredList;

    private AddPrescriptionViewModel viewModel;

    public DrugsAdapter(AddPrescriptionViewModel shareViewModel, List<SimpleDrug> drugs, OnDrugClickListener listener) {
//        super(DIFF_CALLBACK);
        this.drugs = drugs;
        this.filteredList = drugs;
        this.listener = listener;
        viewModel = shareViewModel;
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
        holder.bind(filteredList.get(position), viewModel.getSelectedDrugs().getValue());
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

        public void bind(SimpleDrug drug, List<DrugInPres> drugInPresList) {
            this.binding.drugName.setText(drug.getName());
            for(DrugInPres x : drugInPresList) {
                if(drug.getId() == x.getDrug().getId()) {
                    this.binding.drugName.setTextColor(Color.GRAY);
                    this.binding.drugName.setEnabled(false);
                    return;
                }
            }
            this.binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(drug);
                }
            });
        }
    }
}
