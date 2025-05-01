package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.DrugsAdapter;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.DrugInPresRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.databinding.FragmentSelectDrugBinding;
import com.mad.prescriptionmanagementapp.ui.listener.OnItemClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

public class SelectDrugFragment extends Fragment implements OnItemClickListener<DrugResponse> {

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
        // Tạo Adapter và set cho RecyclerView
        // (Tùy chọn) Thêm SwipeRefreshLayout để refresh thủ công
//        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Log.d("SelectDrugFragment", "Manual refresh triggered");
//            viewModel.forceRefreshDrugs();
//            // Tắt icon refreshing khi LiveData isLoading báo false (trong observeViewModel)
//        });

        return this.binding.getRoot();
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
        this.binding.setViewModel(viewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }
    private void setAdapter() {
        this.drugsAdapter = new DrugsAdapter(this.viewModel.getDrugsList().getValue(), this);
        this.recyclerView.setLayoutManager( new LinearLayoutManager(this.getContext()));
        this.recyclerView.setAdapter(drugsAdapter);
        // Thêm ItemDecoration nếu muốn có đường kẻ phân cách
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
        viewModel.getDrugsList().observe(getViewLifecycleOwner(), drug -> {
            if (drug != null) {
                Log.d("SelectDrugFragment", "Medication list updated from cache. Size: " + drug.size());
                this.setAdapter();
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

    // Xử lý khi một thuốc được chọn trong Adapter
//    @Override
//    public void onMedicationClick(Medication medication) {
//        Log.d("SelectDrugFragment", "Medication clicked: " + medication.name + " (ID: " + medication.id + ")");
//        // Chuyển sang màn hình AddDrugScheduleFragment, truyền ID và Tên thuốc
//        // Sử dụng Safe Args (khuyến nghị)
//        SelectDrugFragmentDirections.ActionSelectDrugFragmentToAddDrugScheduleFragment action =
//                SelectDrugFragmentDirections.actionSelectDrugFragmentToAddDrugScheduleFragment(
//                        medication.id,
//                        medication.name
//                );
//        NavHostFragment.findNavController(this).navigate(action);
//
//        /* // Hoặc dùng Bundle thủ công
//        Bundle args = new Bundle();
//        args.putInt("selectedDrugId", medication.id);
//        args.putString("selectedDrugName", medication.name);
//        NavHostFragment.findNavController(this)
//               .navigate(R.id.action_selectDrugFragment_to_addDrugScheduleFragment, args);
//        */
//    }

    @Override
    public void onItemClick(DrugResponse drug) {
        Fragment newFragment = AddScheduleFragment.newInstance(drug.getId());
        this.viewModel.addDrug(new DrugInPresRequest(drug.getId()));
        // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
        this.getParentFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                .commit();
    }
}