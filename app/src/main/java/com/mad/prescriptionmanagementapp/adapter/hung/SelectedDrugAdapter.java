package com.mad.prescriptionmanagementapp.adapter.hung;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.databinding.ViewholderSelectedDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnSelectedDrugClickListener;

import java.util.List;

public class SelectedDrugAdapter extends RecyclerView.Adapter<SelectedDrugAdapter.SelectedDrugViewHolder> {

    private final OnSelectedDrugClickListener listener;
    private List<DrugInPres> selectedDrugList;
    public SelectedDrugAdapter(List<DrugInPres> selectedDrugList, OnSelectedDrugClickListener listener) {
        this.selectedDrugList = selectedDrugList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectedDrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderSelectedDrugBinding binding = ViewholderSelectedDrugBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SelectedDrugAdapter.SelectedDrugViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedDrugViewHolder holder, int position) {
        DrugInPres currentItem = selectedDrugList.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return selectedDrugList.size();
    }
    public static class SelectedDrugViewHolder extends RecyclerView.ViewHolder {
        ViewholderSelectedDrugBinding binding;

        private final OnSelectedDrugClickListener clickListener;
        private TimeDosageWithDrugAdapter timeDosageWithDrugAdapter;

        public SelectedDrugViewHolder(ViewholderSelectedDrugBinding binding, OnSelectedDrugClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = listener;

        }

        private void setAdapter(List<TimeDosage> timeDosageList, String unit) {
            this.timeDosageWithDrugAdapter = new TimeDosageWithDrugAdapter(timeDosageList, unit);
            this.binding.rcvDrugTimeDosage.setLayoutManager( new LinearLayoutManager(this.binding.rcvDrugTimeDosage.getContext()));
            this.binding.rcvDrugTimeDosage.setAdapter(this.timeDosageWithDrugAdapter);
            // Thêm ItemDecoration nếu muốn có đường kẻ phân cách
        }

        public void bind(DrugInPres item) {
            this.setAdapter(item.getTimeDosages(), item.getUnit().getName());
            this.binding.drugName.setText(item.getSimpleDrug().getName());
            if (clickListener != null) {
                this.binding.btnEdit.setOnClickListener(v -> {
                    clickListener.onDrugClick(item);
                });

            }

        }
    }
}