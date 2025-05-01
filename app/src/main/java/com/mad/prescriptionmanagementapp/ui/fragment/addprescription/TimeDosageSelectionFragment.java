package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.DrugInPresRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.TimeDosageRequest;
import com.mad.prescriptionmanagementapp.databinding.FragmentTimeDosageSelectionBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class TimeDosageSelectionFragment extends Fragment {

    private static final String ARG_DRUG_ID = "drug_id";
    private Long drugId;

    private FragmentTimeDosageSelectionBinding binding;
    private AddPrescriptionViewModel viewModel;

    public TimeDosageSelectionFragment() {
    }

    public static TimeDosageSelectionFragment newInstance(Long drugId) {
        TimeDosageSelectionFragment fragment = new TimeDosageSelectionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, drugId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.drugId = this.getArguments().getLong(ARG_DRUG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentTimeDosageSelectionBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated (@NotNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        this.setupDosage();
        this.setupFinishBtn();
    }
    private void initViewModel() {
        this.viewModel = new ViewModelProvider(this).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupDosage() {
        DrugInPresRequest drugInPres = this.viewModel.getPrescription().getDrugs().stream().filter(drug -> drug.getId() == this.drugId).findFirst().orElse(null);
        if(drugInPres != null) {
            this.binding.edtDosage.setText(drugInPres.getTimeDosages().toString());
        }
    }

    private void setupFinishBtn() {

        this.binding.btnSaveTimeAndDosage.setOnClickListener(v -> {
            int hour = this.binding.timePicker.getHour();
            int minute = this.binding.timePicker.getMinute();
            int dosage = Integer.parseInt(this.binding.edtDosage.getText().toString());

            // Ví dụ: format giờ và in log
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            Log.d("TimeDosage", "Giờ: " + time + ", Liều: " + dosage);
            for(DrugInPresRequest drug : this.viewModel.getPrescription().getDrugs()) {
                if(drug.getId() == this.drugId) {
                    drug.addTimeDosage(new TimeDosageRequest(time, dosage));
                }
            }
            this.setSchedule();
        });
        

    }

    private void setSchedule() {
        Fragment newFragment = AddScheduleFragment.newInstance(drugId);
        // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
        this.getParentFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                .commit();
    }
}