package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.DrugsAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.databinding.FragmentSelectDrugBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ConfirmDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.util.FragmentName;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.ArrayList;

public class SelectDrugFragment extends Fragment implements OnDrugClickListener {

    private static final String ARG_DRUG = "drug";

    private FragmentSelectDrugBinding binding;
    private AddPrescriptionViewModel viewModel;
    private RecyclerView recyclerView;
    private DrugsAdapter drugsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentSelectDrugBinding.inflate(inflater, container, false);
        this.recyclerView  = this.binding.medicationRecycler;
        this.initViewModel();
        this.observeViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setAdapter();
        this.setupSearch();
        ((AddPrescriptionActivity)this.requireActivity()).setCustomTitle("Chọn thuốc");
    }

    private void setupSearch() {
        this.binding.searchDrug.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                drugsAdapter.filter(s.toString());
            }
        });
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
        this.binding.setViewModel(viewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }
    private void setAdapter() {
        this.drugsAdapter = new DrugsAdapter(new ArrayList<>(), this);
        this.recyclerView.setLayoutManager( new LinearLayoutManager(this.getContext()));
        this.recyclerView.setAdapter(drugsAdapter);
        // Thêm ItemDecoration nếu  muốn có đường kẻ phân cách
    }




    private void observeViewModel() {

        // Quan sát LiveData từ ViewModel
//        this.viewModel.getDrugsList().observe(getViewLifecycleOwner(), drugs -> {
//            if (drugs != null && !drugs.isEmpty()) {
//                // Gọi adapter để hiển thị dữ liệu trong RecyclerView
//                DrugsAdapter adapter = new DrugsAdapter(drugs);
//                recyclerView.setAdapter(adapter);
//            }
//        });

        // Quan sát danh sách thuốc từ cache
        viewModel.getOriginalDrugList().observe(getViewLifecycleOwner(), drug -> {
            if (drug != null) {
                Log.d("SelectDrugFragment", "Medication list updated from cache. Size: " + drug.size());
                this.drugsAdapter.updateData(drug);
            } else {
//                Log.w("SelectDrugFragment", "Medication list is null");
//                binding.textViewEmptyList.setVisibility(View.VISIBLE);
            }
//            // Đã có dữ liệu (hoặc danh sách rỗng), ẩn ProgressBar chính
//            binding.progressBarLoading.setVisibility(View.GONE);
//            // Tắt SwipeRefreshLayout nếu nó đang chạy (đảm bảo)
//            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }
    @Override
    public void onItemClick(SimpleDrug drug) {
        if(this.viewModel.existedDrug(drug.getId())) {
            ConfirmDialog.showCancelConfirmationDialog(requireContext(), new ConfirmDialog.ConfirmationDialogListener() {
                @Override
                public void onConfirm() {
                    moveToNextFragment(drug);
                }
                @Override
                public void onCancel() {
                }
            }, "Xác nhận", "Bạn đã thêm lịch cho thuốc này, tiếp tục thêm ", "Tiếp tục");
        } else {
            moveToNextFragment(drug);
        }
    }

    private void moveToNextFragment(SimpleDrug drug) {
        Fragment newFragment = AddScheduleFragment.newInstance(drug.getId(), false);
        this.viewModel.setCurrentDrug(new DrugInPres(drug));
        Tools.changeFragment(this.requireActivity(), newFragment, FragmentName.SD_TO_AS);
    }
}


