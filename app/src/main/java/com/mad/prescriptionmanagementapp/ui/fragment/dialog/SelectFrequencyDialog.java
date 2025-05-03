package com.mad.prescriptionmanagementapp.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.ScheduleRequest;
import com.mad.prescriptionmanagementapp.databinding.DialogSelectFrequencyBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.addprescription.TimeDosageSelectionFragment;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SelectFrequencyDialog extends DialogFragment {

    private static final String ARG_DRUG_ID = "drug_id";

    private Long drugId;

    private DialogSelectFrequencyBinding binding;
    private AddPrescriptionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.binding = DialogSelectFrequencyBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupRadio();
        this.setupBtnIncrease();
        this.setupBtnDecrease();
        this.setupBtnSelect();
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(this).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupRadio() {
        if(this.viewModel.getFrequencyId() == null) {
            this.viewModel.setFrequencyId(this.binding.radioDaily.getId());
        }
        this.binding.radioGroupFrequency.check(this.viewModel.getFrequencyId());
        this.updateLayoutVisibility(this.viewModel.getFrequencyId());


        this.binding.radioGroupFrequency.setOnCheckedChangeListener((group, checkedId) -> {
            this.updateLayoutVisibility(checkedId);
        });
    }

    private void updateLayoutVisibility(int checkedId) {
        binding.numberOfDaysLayout.setVisibility(View.GONE);
        binding.daysOfWeekLayout.setVisibility(View.GONE);

        if (checkedId == R.id.radioEveryNDays) {
            binding.numberOfDaysLayout.setVisibility(View.VISIBLE);
        } else if (checkedId == R.id.radioSpecificDays) {
            binding.daysOfWeekLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupBtnIncrease() {
        this.binding.btnIncrease.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(binding.tvCount.getText().toString());
            binding.tvCount.setText(String.valueOf(currentValue + 1));
        });
    }

    private void setupBtnDecrease() {
        this.binding.btnDecrease.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(binding.tvCount.getText().toString());
            if (currentValue > 1) { // Đảm bảo không giảm dưới 1
                binding.tvCount.setText(String.valueOf(currentValue - 1));
            }
        });
    }

    private void setupBtnSelect() {
        this.binding.btnContinue.setOnClickListener(v -> {
            String result = "";
            int checkedId = binding.radioGroupFrequency.getCheckedRadioButtonId();

//            if (checkedId == binding.radioDaily.getId()) {
//                result = "Uống hằng ngày";
//            } else if (checkedId == binding.radioEveryNDays.getId()) {
//                result = "Uống cách " + binding.edtNumberOfDays.getText().toString() + " ngày";
//            } else {
//                result = "Uống vào các ngày: ";
//                result += getSelectedDays();
//            }
//
//            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
//            for(DrugInPres drug : this.viewModel.getPrescription().getDrugs()) {
//                if(Objects.equals(drug.getId(), this.drugId)) {
//                    drug.setSchedules(this.generateSchedules(checkedId, LocalDate.now()));
//                    break;
//                }
//            }
            this.setTimeDosageSelection();
        });
    }

    private List<ScheduleRequest> generateSchedules(int checkedId, LocalDate startDate) {
        List<ScheduleRequest> schedules = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (checkedId == binding.radioDaily.getId()) {
            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i);
                schedules.add(new ScheduleRequest(date.format(dateFormatter), "Uống hằng ngày"));
            }
        } else if (checkedId == binding.radioEveryNDays.getId()) {
            int gap = Integer.parseInt(binding.tvCount.getText().toString());
            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i * gap);
                schedules.add(new ScheduleRequest(date.format(dateFormatter), "Uống cách " + gap + " ngày"));
            }
        } else {
            List<Integer> selectedDays = getSelectedDaysIndexes(); // 1 = Monday, 7 = Sunday
            LocalDate date = startDate;
            int count = 0;
            while (count < 30) {
                if (selectedDays.contains(date.getDayOfWeek().getValue())) {
                    schedules.add(new ScheduleRequest(date.format(dateFormatter), "Uống vào các ngày cụ thể"));
                    count++;
                }
                date = date.plusDays(1);
            }
        }

        return schedules;
    }

    private List<Integer> getSelectedDaysIndexes() {
        List<Integer> days = new ArrayList<>();
        if (binding.cbMonday.isChecked()) days.add(1);
        if (binding.cbTuesday.isChecked()) days.add(2);
        if (binding.cbWednesday.isChecked()) days.add(3);
        if (binding.cbThursday.isChecked()) days.add(4);
        if (binding.cbFriday.isChecked()) days.add(5);
        if (binding.cbSaturday.isChecked()) days.add(6);
        if (binding.cbSunday.isChecked()) days.add(7);
        return days;
    }

    private void setTimeDosageSelection() {
        Fragment newFragment = TimeDosageSelectionFragment.newInstance(this.drugId);
        // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
        this.getParentFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                .commit();
    }




    private String getSelectedDays() {
        StringBuilder days = new StringBuilder();
        CheckBox[] ids = {binding.cbMonday, binding.cbTuesday, binding.cbWednesday, binding.cbThursday, binding.cbFriday, binding.cbSaturday, binding.cbSunday};
        String[] labels = {"Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy", "Chủ nhật"};

        for (int i = 0; i < ids.length; i++) {
            if (ids[i].isChecked()) {
                days.append(labels[i]).append(", ");
            }
        }

        if (days.length() > 0) {
            days.setLength(days.length() - 2); // remove last comma
        } else {
            days.append("Không có ngày nào được chọn");
        }

        return days.toString();
    }
}
