package com.mad.prescriptionmanagementapp.ui.fragment.dialog;

import static com.mad.prescriptionmanagementapp.util.Tools.formatNumber;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.fragment.app.DialogFragment;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.databinding.DialogAddTimeAndDosageBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddScheduleViewModel;

import org.jetbrains.annotations.NotNull;

public class SelectTimeAndDosageDialog extends DialogFragment {

    private DialogAddTimeAndDosageBinding binding;
    private final AddScheduleViewModel parentViewModel;
    private TimeDosage currentTimeDosage;

    public SelectTimeAndDosageDialog(TimeDosage curentTimeDosage, AddScheduleViewModel parentViewModel ) {
        this.currentTimeDosage = curentTimeDosage;
        this.parentViewModel = parentViewModel;
    }

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
        this.binding = DialogAddTimeAndDosageBinding.inflate(inflater, container, false);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
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
    }

    private void setupTimeDosage() {
        this.binding.unit.setText(this.parentViewModel.getUnit().getValue().toString());
        TimeDosage timeDosage = this.currentTimeDosage;
        if (timeDosage != null) {
            int hour = timeDosage.getHour();   // hoặc timeUse.get(Calendar.HOUR_OF_DAY)
            int minute = timeDosage.getMinutes(); // hoặc timeUse.get(Calendar.MINUTE)
            this.binding.timePicker.setHour(hour);
            this.binding.timePicker.setMinute(minute);
            this.binding.tvCount.setText(formatNumber(timeDosage.getDosage()));
        }
        this.binding.timePicker.setIs24HourView(true);
    }

    private void setupFinishBtn() {

        this.binding.btnSave.setOnClickListener(v -> {
            int hour = this.binding.timePicker.getHour();
            int minute = this.binding.timePicker.getMinute();
            double dosage = Double.parseDouble(this.binding.tvCount.getText().toString());
            int validate = this.validateNewTime(hour, minute);
            TimeDosage newTimeDosage = new TimeDosage(hour, minute, dosage);
             if (validate == 0) {
                new ErrorDialog(this, "Khung giờ đã tồn tại", false).showDialog();
            } else if (validate < 121) {
                ConfirmDialog.showCancelConfirmationDialog(requireContext(), new ConfirmDialog.ConfirmationDialogListener() {
                    @Override
                    public void onConfirm() {
                        handleFinishBtn(newTimeDosage);
                    }
                }, "Xác nhận", String.format("2 khung giờ chỉ cách nhau %d phút, bạn có muốn tiếo tục?", validate), "Tạo");
            } else {
                 handleFinishBtn(newTimeDosage);
            }
        });
    }

    private void handleFinishBtn(TimeDosage newTimeDosgae) {
        if(SelectTimeAndDosageDialog.this.currentTimeDosage == null) {
            SelectTimeAndDosageDialog.this.parentViewModel.addTimeDosage(newTimeDosgae);
        } else {
            currentTimeDosage.copy(newTimeDosgae);
            this.parentViewModel.updateADosage();
        }
        this.dismiss();
    }

    private int validateNewTime(int hour, int minute) {
        for (TimeDosage x : this.parentViewModel.getListTimeDosage().getValue()) {
            if(currentTimeDosage != null
                && x.getHour() == currentTimeDosage.getHour()
                && x.getMinutes() == currentTimeDosage.getMinutes()) {
                continue;
            }
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
        }
    }
