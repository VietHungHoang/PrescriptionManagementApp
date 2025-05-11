package com.mad.prescriptionmanagementapp.ui.fragment.dialog;

import static com.mad.prescriptionmanagementapp.util.Tools.formatNumber;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.databinding.DialogAddTimeAndDosageBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.util.ErrorToast;

import org.jetbrains.annotations.NotNull;

public class SelectTimeAndDosageDialog extends DialogFragment {

    private DialogAddTimeAndDosageBinding binding;
    private AddPrescriptionViewModel viewModel;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM); // Xuất hiện từ dưới
            window.setWindowAnimations(R.style.MyDialogAnimation); // Gắn animation
            window.setBackgroundDrawableResource(R.drawable.dialog_background);
            window.setClipToOutline(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = DialogAddTimeAndDosageBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        this.setupTimeDosage();
        this.setupFinishBtn();
        this.setupBtnBack();
        this.setupBtnDecrease();
        this.setupBtnIncrease();
        this.binding.timePicker.setIs24HourView(true);
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
//        this.binding.setViewModel(viewModel);
//        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupTimeDosage() {
        this.binding.unit.setText(this.viewModel.getDrugUnit().getValue());
        TimeDosage timeDosage = this.viewModel.getCurrentTimeDosage();
        if (timeDosage != null) {
            int hour = timeDosage.getHour();   // hoặc timeUse.get(Calendar.HOUR_OF_DAY)
            int minute = timeDosage.getMinutes(); // hoặc timeUse.get(Calendar.MINUTE)
            this.binding.timePicker.setHour(hour);
            this.binding.timePicker.setMinute(minute);
            this.binding.tvCount.setText(formatNumber(timeDosage.getDosage()));
        }
    }

    private void setupFinishBtn() {

        this.binding.btnSave.setOnClickListener(v -> {
            int hour = this.binding.timePicker.getHour();
            int minute = this.binding.timePicker.getMinute();
            double dosage = Double.parseDouble(this.binding.tvCount.getText().toString());
            int validate = this.validateNewTime(hour, minute);
             if (validate == 0) {
                new ErrorDialog(this, "Khung giờ đã tồn tại", false).showDialog();
            } else if (validate < 121) {
                ConfirmDialog.showCancelConfirmationDialog(requireContext(), new ConfirmDialog.ConfirmationDialogListener() {
                    @Override
                    public void onConfirm() {
                        viewModel.setTimeAndDosage(new TimeDosage(hour, minute, dosage));
                        dismiss();
                    }
                    @Override
                    public void onCancel() {
                    }
                }, "Xác nhận", String.format("2 khung giờ chỉ cách nhau %d phút, bạn có muốn tiếo tục?", validate), "Tạo");
            } else {
                 this.viewModel.setTimeAndDosage(new TimeDosage(hour, minute, dosage));
                 this.dismiss();
            }
        });
    }

    private int validateNewTime(int hour, int minute) {
        for (TimeDosage x : this.viewModel.getListTimeDosage().getValue()) {
            int check = Math.abs(x.getHour() * 60 + x.getMinutes() - hour * 60 - minute);
            if(check < 121) {
                return check;
            }
        }
        return 121;

    }

        private void setupBtnIncrease () {
            this.binding.btnIncrease.setOnClickListener(v -> {
                double currentValue = Double.parseDouble(binding.tvCount.getText().toString());
                binding.tvCount.setText(formatNumber(currentValue + 0.25));
            });
        }

        private void setupBtnDecrease () {
            this.binding.btnDecrease.setOnClickListener(v -> {
                double currentValue = Double.parseDouble(binding.tvCount.getText().toString());
                if (currentValue > 0.25) { // Đảm bảo không giảm dưới 1
                    binding.tvCount.setText(formatNumber(currentValue - 0.25));
                }
            });
        }


        private void setupBtnBack () {
            this.binding.btnBack.setOnClickListener(v -> {
                this.dismiss();
            });
        }

        @Override
        public void onDestroyView () {
            super.onDestroyView();
            this.viewModel.setCurrentTimeDosage(null);
        }
    }
