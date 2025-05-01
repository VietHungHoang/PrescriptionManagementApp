package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddScheduleBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.util.Calendar;
import java.util.List;

public class AddScheduleFragment extends Fragment {

    private static final String ARG_DRUG_ID = "drug_id";
    private DrugResponse drug;

    private FragmentAddScheduleBinding binding;
    private AddPrescriptionViewModel viewModel;
    private AutoCompleteTextView spinnerUnit;
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
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thực hiện các thao tác với View sau khi View đã được tạo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.unit_array,
                android.R.layout.simple_dropdown_item_1line
        );

        this.spinnerUnit.setAdapter(adapter);

        this.spinnerUnit.setOnTouchListener((v, event) -> {
            if (spinnerUnit.getDropDownHeight() > 0) {
                spinnerUnit.dismissDropDown();
            } else {
                spinnerUnit.showDropDown();
            }
            return true;
        });

        this.setStartDate();
        this.binding.spinnerFrequency.setOnClickListener(v -> {
                    setFrequencySelection(this.drug.getId());
                });
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                .commit();
    }

    private void setAddTimeAndDosage() {

    }


}