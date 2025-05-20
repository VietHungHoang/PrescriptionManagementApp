package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.TimeAndDosageAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddScheduleBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ConfirmDialog;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ErrorDialog;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.SelectTimeAndDosageDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnTimeDosageClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddScheduleViewModel;
import com.mad.prescriptionmanagementapp.util.FragmentName;
import com.mad.prescriptionmanagementapp.util.Frequency;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.List;

public class AddScheduleFragment extends Fragment {
    private static final String ARG_CUR_DRUG = "current_drug";
    private static final String ARG_IS_EDIT = "is_edit";
    private FragmentAddScheduleBinding binding;
    private AddPrescriptionViewModel shareViewModel;
    private AddScheduleViewModel viewModel;
    private TimeAndDosageAdapter timeAndDosageAdapter;
    private DrugInPres currentDrug;
    private boolean isEdit;

    private boolean isEdited;

    public static AddScheduleFragment newInstance(DrugInPres drug, boolean isEdit) {
        AddScheduleFragment fragment = new AddScheduleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CUR_DRUG, drug);
        args.putBoolean(ARG_IS_EDIT, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.currentDrug = getArguments().getParcelable(ARG_CUR_DRUG);
            this.isEdit = getArguments().getBoolean(ARG_IS_EDIT);
        }
        this.isEdited = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddScheduleBinding.inflate(inflater, container, false);
        this.initViewModel();
        this.observeViewModel();
        return this.binding.getRoot();
    }

    private void initViewModel() {
        this.shareViewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        this.viewModel = new ViewModelProvider(this).get(AddScheduleViewModel.class);
        this.binding.setViewModel(viewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeViewModel() {
        // Quan sát danh sách thuốc từ cache
//        shareViewModel.getCurrentDrug().observe(getViewLifecycleOwner(), drug -> {
//            if (drug != null) {
////                Log.d("SelectDrugFragment", "Medication list updated from cache. Size: " + drug.size());
//                this.setAdapter();
//            }
//        });

        this.viewModel.getFrequency().observe(getViewLifecycleOwner(), frequency -> {
                this.updateFrequencyView(frequency);
        });

        this.viewModel.getListTimeDosage().observe(getViewLifecycleOwner(), timeDosages -> {
            if (timeDosages != null) {
                this.timeAndDosageAdapter.notifyDataSetChanged();
            }
        });

        this.viewModel.getUnit().observe(getViewLifecycleOwner(), unit -> {
            this.timeAndDosageAdapter.notifyDataSetChanged();
        });

        this.viewModel.getUnitList().observe(getViewLifecycleOwner(), unitList -> {
            ArrayAdapter<Unit> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, unitList);
            this.binding.spinnerUnit.setAdapter(adapter);
            this.binding.spinnerUnit.setSelection((this.currentDrug != null && this.currentDrug.getUnit() != null) ? this.currentDrug.getUnit().getId().intValue() - 1 : 3);
            this.binding.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Unit tmo = (Unit) parent.getItemAtPosition(position);
                    AddScheduleFragment.this.viewModel.setUnit(tmo);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupUI();
        this.setOnClickView();
        this.setOnFocusView();
        this.setAddTimeAndDosage();
        this.setTimeDosageAdapter();
        this.setBack();
    }

    private void setupUI() {
        this.viewModel.setCurrentDrug(this.currentDrug);
        if (this.isEdit) {
            this.binding.edtStartDate.setText(this.currentDrug.getStartDate());
            this.binding.uiDeleteDrug.setVisibility(View.VISIBLE);
            this.binding.btnDelete.setOnClickListener(v -> {
                ConfirmDialog.showConfirmationDialog(requireContext(), "Xác nhận", "Bạn chắc chắn muốn xoá thuốc này khỏi đơn", "Xoá", "Huỷ", new ConfirmDialog.ConfirmationDialogListener() {
                    @Override
                    public void onConfirm() {
                        AddScheduleFragment.this.shareViewModel.removeCurrentDrug(AddScheduleFragment.this.viewModel.getCurrentDrug().getValue());
                        Fragment newFragment = AddPrescriptionInfoFragment.newInstance();
                        Tools.replaceFragment(AddScheduleFragment.this.requireActivity(), newFragment, null);
                    }
                });
            });
        }
    }

    private void setOnClickView() {
        this.binding.spinnerFrequency.setOnClickListener(v -> {
            this.moveToFrequencySelection();
        });

        this.binding.btnLuu.setOnClickListener(v -> {
            if (this.viewModel.getListTimeDosage().getValue().isEmpty()) {
                new ErrorDialog(this, "Vui lòng thêm thời gian uống thuốc", true).showDialog();
            } else {
                DrugInPres drug = this.viewModel.getDrugToSave(this.binding.edtStartDate.getText().toString(), this.binding.edtNote.getText().toString());
                if(this.isEdit) {
                    this.shareViewModel.updateSelectedDrug(this.currentDrug, drug);
                    this.requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    this.shareViewModel.addSelectedDrug(drug);
                    this.viewModel.setCurrentDrug(new DrugInPres());
                    this.requireActivity().getSupportFragmentManager().popBackStack(FragmentName.SD_TO_AS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    this.requireActivity().getSupportFragmentManager().popBackStack(FragmentName.API_TO_SD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        Tools.setupDatePickerDialog(this.requireContext(), this.binding.edtStartDate, true, this.currentDrug.getStartDate());
    }

    private void setOnFocusView() {
        this.binding.edtNote.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.viewModel.setNote(text);
            }
        });
    }

    private void moveToFrequencySelection() {
        Fragment newFragment = FrequencySelectionFragment.newInstance(this.viewModel);
        Tools.addFragment(this, newFragment, null);
    }

    private void setAddTimeAndDosage() {
        this.binding.btnAddTimeAndDosage.setOnClickListener(v -> {
            new SelectTimeAndDosageDialog(null, this.viewModel).show(getParentFragmentManager(), "add_time_and_dosage");
        });
    }

    private void updateFrequencyView(Frequency frequency) {
        switch (frequency) {
            case DAILY:
                this.binding.spinnerFrequency.setText("Mỗi ngày");
                break;
            case EVERY_N_DAYS:
                String day = String.format("Cách %d ngày", this.viewModel.getDayBetween());
                this.binding.spinnerFrequency.setText(day);
                break;
            case SPECIFIC_DAYS:
                this.binding.spinnerFrequency.setText(this.getSelectedDays(this.viewModel.getSpecificDays()));
        }
    }

    private String getSelectedDays(List<Integer> dayIds) {
        StringBuilder days = new StringBuilder();
        String[] labels = {"Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy", "Chủ nhật"};

        for (Integer x : dayIds) {
            days.append(labels[x - 1]).append(", ");
        }

        if (days.length() > 0) {
            days.setLength(days.length() - 2); // remove last comma
        } else {
            days.append("Không có ngày nào được chọn");
        }

        return days.toString();
    }

    private void setTimeDosageAdapter() {
        this.timeAndDosageAdapter = new TimeAndDosageAdapter(this.viewModel.getListTimeDosage().getValue(), this.viewModel, new OnTimeDosageClickListener() {
            @Override
            public void onItemClick(TimeDosage timeDosage) {
                new SelectTimeAndDosageDialog(timeDosage, AddScheduleFragment.this.viewModel).show(getParentFragmentManager(), "add_time_and_dosage");
            }

            @Override
            public void onRemoveButtonClick(int position) {
                AddScheduleFragment.this.viewModel.removeTimeDosage(position);
                AddScheduleFragment.this.timeAndDosageAdapter.notifyDataSetChanged();
            }
        });
        this.binding.rcvTimeAndDosage.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.binding.rcvTimeAndDosage.setAdapter(this.timeAndDosageAdapter);
        // Thêm ItemDecoration nếu muốn có đường kẻ phân cách
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setBack() {
        this.requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if(isEdit) {
                            ConfirmDialog.showCancelConfirmationDialog(requireContext(), new ConfirmDialog.ConfirmationDialogListener() {
                                @Override
                                public void onConfirm() {
                                    AddScheduleFragment.this.requireActivity().getSupportFragmentManager().popBackStack();
                                }
                            }, "Huỷ", "Tất cả thông tin bạn đã nhập sẽ bị xoá, đồng ý huỷ", "Đồng ý");
                        }
                        else {
                            AddScheduleFragment.this.requireActivity().getSupportFragmentManager().popBackStack();
                        }
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });
    }
}