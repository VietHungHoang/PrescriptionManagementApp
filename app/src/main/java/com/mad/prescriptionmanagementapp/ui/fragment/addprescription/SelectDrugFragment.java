package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.adapter.hung.DrugsAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.databinding.FragmentSelectDrugBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ConfirmDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.SelectDrugViewModel;
import com.mad.prescriptionmanagementapp.util.FragmentName;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.ArrayList;

public class SelectDrugFragment extends Fragment {
    private FragmentSelectDrugBinding binding;
    private AddPrescriptionViewModel shareViewModel;
    private SelectDrugViewModel viewModel;
    private DrugsAdapter drugsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentSelectDrugBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    private void initViewModel() {
        this.shareViewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        this.viewModel = new ViewModelProvider(requireActivity()).get(SelectDrugViewModel.class);
        this.binding.setViewModel(shareViewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setAdapter();
        this.setupSearch();
        this.observeViewModel();
    }

    private void setAdapter() {
        this.drugsAdapter = new DrugsAdapter(this.shareViewModel, new ArrayList<>(), new OnDrugClickListener() {
            @Override
            public void onItemClick(SimpleDrug drug) {
                if (SelectDrugFragment.this.shareViewModel.existedDrug(drug.getId())) {
                    ConfirmDialog.showCancelConfirmationDialog(requireContext(), new ConfirmDialog.ConfirmationDialogListener() {
                        @Override
                        public void onConfirm() {
                            SelectDrugFragment.this.moveToNextFragment(drug);
                        }
                    }, "Xác nhận", "Bạn đã thêm lịch cho thuốc này, tiếp tục thêm ", "Tiếp tục");
                } else {
                   SelectDrugFragment.this.moveToNextFragment(drug);
                }
            }
        });
        this.binding.medicationRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.binding.medicationRecycler.setAdapter(drugsAdapter);
    }

    private void setupSearch() {
        this.binding.searchDrug.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                drugsAdapter.filter(s.toString());
            }
        });

        this.binding.btnAddDrug.setOnClickListener(v -> {

        });
    }

    private void observeViewModel() {
       // Quan sát danh sách thuốc từ cache
       this.viewModel.getOriginalDrugList().observe(getViewLifecycleOwner(), drug -> {
            if (drug != null) {
                this.drugsAdapter.updateData(drug);
            } else {
                Log.w("SelectDrugFragment", "Medication list is null");
            }
        });
    }

    private void moveToNextFragment(SimpleDrug drug) {
        Fragment newFragment = AddScheduleFragment.newInstance(new DrugInPres(drug), false);
        Tools.replaceFragment(this.requireActivity(), newFragment, FragmentName.SD_TO_AS);
    }
}


