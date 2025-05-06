package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.ScheduleRequest;
import com.mad.prescriptionmanagementapp.databinding.FragmentFrequencySelectionBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.util.Frequency;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FrequencySelectionFragment extends Fragment {
    private static final String ARG_DRUG_ID = "drug_id";

    private Long drugId;

    private FragmentFrequencySelectionBinding binding;
    private AddPrescriptionViewModel viewModel;



    public static FrequencySelectionFragment newInstance(Long drugId) {
       FrequencySelectionFragment fragment = new FrequencySelectionFragment();
       Bundle args = new Bundle();
       args.putLong(ARG_DRUG_ID, drugId);
       fragment.setArguments(args);
       return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments() != null) {
            this.drugId = this.getArguments().getLong(ARG_DRUG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentFrequencySelectionBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated (@NotNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        this.setupRadio();
        this.setupBtnIncrease();
        this.setupBtnDecrease();
        this.setupBtnSelect();
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupRadio() {
            if(this.viewModel.getFrequency() != null) {
                this.updateLayoutVisibility(this.viewModel.getFrequency());
            }
            else {
                this.binding.radioGroupFrequency.check(this.binding.radioDaily.getId());
            }



        this.binding.radioGroupFrequency.setOnCheckedChangeListener((group, checkedId) -> {
            binding.numberOfDaysLayout.setVisibility(checkedId == this.binding.radioEveryNDays.getId() ? View.VISIBLE : View.GONE);
            binding.daysOfWeekLayout.setVisibility(checkedId == this.binding.radioSpecificDays.getId() ? View.VISIBLE : View.GONE);
        });
    }

    private void updateLayoutVisibility(Frequency frequency) {
        binding.numberOfDaysLayout.setVisibility(View.GONE);
        binding.daysOfWeekLayout.setVisibility(View.GONE);

        switch (frequency) {
            case DAILY:
                this.binding.radioGroupFrequency.check(this.binding.radioDaily.getId());
                break;
            case EVERY_N_DAY:
                this.binding.radioGroupFrequency.check(this.binding.radioEveryNDays.getId());
                this.binding.numberOfDaysLayout.setVisibility(View.VISIBLE);
                this.binding.tvCount.setText(this.viewModel.getDayBetween());
                break;
            case SPECIFIC_DAYS:
                this.binding.radioGroupFrequency.check(this.binding.radioSpecificDays.getId());
                this.binding.daysOfWeekLayout.setVisibility(View.VISIBLE);
                this.setSelectedDays(this.viewModel.getSpecificDays());
                break;
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

            if (checkedId == binding.radioDaily.getId()) {
                this.viewModel.setFrequencyDaily();
            } else if (checkedId == binding.radioEveryNDays.getId()) {
                this.viewModel.setFrequencyEveryNDay(Integer.parseInt(this.binding.tvCount.getText().toString()));
            } else {
                this.viewModel.setFrequencySpecificDay(this.getSelectedDaysIndexes());
            }

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

    private void setSelectedDays(List<Integer> days) {
        CheckBox[] checkBoxes = new CheckBox[]{
                binding.cbMonday, binding.cbTuesday, binding.cbWednesday,
                binding.cbThursday, binding.cbFriday, binding.cbSaturday,
                binding.cbSunday
        };

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setChecked(days.contains(i + 1));
        }
    }

    private void setTimeDosageSelection() {
//        Fragment newFragment = TimeDosageSelectionFragment.newInstance(this.drugId);
//        // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
//        this.getParentFragmentManager()
//                .beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
//                .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
//                .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
//                .commit();
        requireActivity().getSupportFragmentManager().popBackStack();
    }





}