package com.mad.prescriptionmanagementapp.adapter;

import static com.mad.prescriptionmanagementapp.util.Tools.formatNumber;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.databinding.ViewholderSelectedDrugBinding;
import com.mad.prescriptionmanagementapp.databinding.ViewholderTimeDosageWithDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnSelectedDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeDosageWithDrugAdapter extends RecyclerView.Adapter<TimeDosageWithDrugAdapter.TimeDosageWithDrugViewHolder> {

    private List<TimeDosage> timeDosageList;

    public TimeDosageWithDrugAdapter(List<TimeDosage> timeDosageList) {
        this.timeDosageList = timeDosageList;
    }

    @NonNull
    @Override
    public TimeDosageWithDrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderTimeDosageWithDrugBinding binding = ViewholderTimeDosageWithDrugBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TimeDosageWithDrugAdapter.TimeDosageWithDrugViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeDosageWithDrugViewHolder holder, int position) {
        TimeDosage currentItem = timeDosageList.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return timeDosageList.size();
    }

    public static class TimeDosageWithDrugViewHolder extends RecyclerView.ViewHolder {
        ViewholderTimeDosageWithDrugBinding binding;

        public TimeDosageWithDrugViewHolder(ViewholderTimeDosageWithDrugBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TimeDosage item) {
            String time = String.format(Locale.US, "%02d:%02d", item.getHour(), item.getMinutes());
            this.binding.time.setText(time);
            this.binding.dosage.setText(String.format(Locale.US, "%s %s", formatNumber(item.getDosage()), item.getDosage()));
            }
    }
}