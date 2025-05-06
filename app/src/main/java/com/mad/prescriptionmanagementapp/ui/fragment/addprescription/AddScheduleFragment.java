package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.TimeAndDosageAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddScheduleBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.SelectTimeAndDosageDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.listener.OnTimeDosageClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.util.Calendar;
import java.util.List;

public class AddScheduleFragment extends Fragment implements OnTimeDosageClickListener {

    private static final String ARG_DRUG_ID = "drug_id";
    private DrugResponse drug;

    private FragmentAddScheduleBinding binding;
    private AddPrescriptionViewModel viewModel;
    private Spinner spinnerUnit;

    private TimeAndDosageAdapter timeAndDosageAdapter;
    public AddScheduleFragment() {
        // Required empty public constructor
    }

    public static AddScheduleFragment newInstance(Long drugId) {
        AddScheduleFragment fragment = new AddScheduleFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, drugId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddScheduleBinding.inflate(inflater, container, false);
        this.spinnerUnit = this.binding.spinnerUnit;
        this.initViewModel();
        this.observeViewModel();

        if (getArguments() != null) {
            Long drugId = getArguments().getLong(ARG_DRUG_ID);
            List<DrugResponse> drugs=this.viewModel.getDrugsList().getValue();
            this.drug = drugs.stream().filter(drug -> drug.getId() == drugId).findFirst().orElse(null);
        }
        return this.binding.getRoot();
    }
    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
        this.binding.setViewModel(viewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thực hiện các thao tác với View sau khi View đã được tạo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.unit_array,
                R.layout.item_dropdown
        );

        this.spinnerUnit.setAdapter(adapter);
        ((AddPrescriptionActivity)this.requireActivity()).setCustomTitle("Thêm lịch uống thuốc");

//        this.spinnerUnit.setOnTouchListener((v, event) -> {
//            if (spinnerUnit.getDropDownHeight() > 0) {
//                spinnerUnit.dismissDropDown();
//            } else {
//                spinnerUnit.showDropDown();
//            }
//            return true;
//        });

        this.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                viewModel.setDrugUnit(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });


        this.setStartDate();
        this.binding.spinnerFrequency.setOnClickListener(v -> {
                    setFrequencySelection(this.drug.getId());
                });
        this.setAdapter();
        setAddTimeAndDosage();
    }

    private void setStartDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        this.binding.edtStartDate.setText(String.format("%02d/%02d/%d", day, month + 1, year));

        this.binding.edtStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        @SuppressLint("DefaultLocale") String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        this.binding.edtStartDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setFrequencySelection(Long drugId) {
        Fragment newFragment = FrequencySelectionFragment.newInstance(drugId);
        // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
        this.getParentFragmentManager()
                .beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_CLOSE)
                .setCustomAnimations(
                        R.anim.zoom_in,    // Fragment B vào (zoom in)
                        R.anim.fade_out,   // Fragment A ra (fade out) - fragment cũ
                        R.anim.zoom_out,    // Fragment A vào lại khi Back (fade in) - fragment cũ
                        R.anim.fade_out )  // Fragment B ra khi Back (zoom out)
                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                .commit();
//        new SelectFrequencyDialog().show(getParentFragmentManager(), "selectfrequency");
    }

    private void setAddTimeAndDosage() {
        this.binding.btnAddTimeAndDosage.setOnClickListener(v -> {
            new SelectTimeAndDosageDialog().show(getParentFragmentManager(), "add_time_and_dosage");
        });

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

        viewModel.getCurrentDrug().observe(getViewLifecycleOwner(), drugInPres -> {
            if(drugInPres != null) {
                this.updateFrequencyView(drugInPres);
            }
        });

        viewModel.getListTimeDosage().observe(getViewLifecycleOwner(), timeDosages -> {
            if(timeDosages != null) {
                this.timeAndDosageAdapter.notifyItemInserted(timeDosages.size() - 1);
            }
        });

        viewModel.getDrugUnit().observe(getViewLifecycleOwner(), this::onChanged);

    }

    private void updateFrequencyView(DrugInPres drugInPres) {
        switch (drugInPres.getFrequency()) {
            case DAILY:
                this.binding.spinnerFrequency.setText("Mỗi ngày");
                break;
            case EVERY_N_DAY:
                String day = String.format("Cách %d ngày", drugInPres.getEveryNDays());
                this.binding.spinnerFrequency.setText(day);
                break;
            case SPECIFIC_DAYS:
                this.binding.spinnerFrequency.setText(this.getSelectedDays(drugInPres.getSpecificDays()));
        }
    }

    private String getSelectedDays(List<Integer> dayIds) {
        StringBuilder days = new StringBuilder();
        String[] labels = {"Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy", "Chủ nhật"};

        for(Integer x : dayIds) {
            days.append(labels[x - 1]).append(", ");
        }

        if (days.length() > 0) {
            days.setLength(days.length() - 2); // remove last comma
        } else {
            days.append("Không có ngày nào được chọn");
        }

        return days.toString();
    }

        private void setAdapter() {
            this.timeAndDosageAdapter = new TimeAndDosageAdapter(this.viewModel.getListTimeDosage().getValue(), this.viewModel, this);
            this.binding.rcvTimeAndDosage.setLayoutManager( new LinearLayoutManager(this.getContext()));
            this.binding.rcvTimeAndDosage.setAdapter(this.timeAndDosageAdapter);
            // Thêm ItemDecoration nếu muốn có đường kẻ phân cách
        }


    @Override
    public void onItemClick(TimeDosage timeDosage) {
        this.viewModel.setCurrentTimeDosage(timeDosage);
        new SelectTimeAndDosageDialog().show(getParentFragmentManager(), "add_time_and_dosage");
    }

    @Override
    public void onRemoveButtonClick(int position) {
        this.viewModel.removeTimeDosage(position);
        this.timeAndDosageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void onChanged(String unit) {
        if (unit != null) {
            this.timeAndDosageAdapter.notifyDataSetChanged();
        }
    }
}