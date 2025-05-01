package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
public class AddPrescriptionInfoFragment extends Fragment {
    private FragmentAddPrescriptionInfoBinding binding;
    private AddPrescriptionViewModel viewModel;

    public AddPrescriptionInfoFragment() {
    }

    public static AddPrescriptionInfoFragment newInstance() {
        AddPrescriptionInfoFragment fragment = new AddPrescriptionInfoFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddPrescriptionInfoBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AddPrescriptionActivity) requireActivity()).setCustomTitle("Thêm đơn thuốc");
        // Kiểm tra trạng thái của switch Kham benh
        this.enableMedicalInfo(this.binding.switchMedicalInfo.isChecked());
        this.setOnclickBtnAddDrug();
        this.setListenerSwitchMedicalInfo();
        this.setOnclickBtnSave();
        this.observeError();
        this.binding.edtPrescriptionName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                this.viewModel.resetErrorMessage();
            }
        });
    }


    private void setOnclickBtnAddDrug() {
        this.binding.btnAddDrug.setOnClickListener(v -> {
            Fragment newFragment = new SelectDrugFragment();
            this.getParentFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_CLOSE)
                    .replace(R.id.fragment_container, newFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setListenerSwitchMedicalInfo() {

        // Listener cho Switch "Thêm thông tin khám bệnh"
        this.binding.switchMedicalInfo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.enableMedicalInfo(this.binding.switchMedicalInfo.isChecked());
        });
    };

    private void enableMedicalInfo(boolean isEnabled) {
        this.binding.cardMedicalInfo.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    private void setOnclickBtnSave() {
        this.binding.btnSave.setOnClickListener(v -> {
            this.viewModel.handleBtnSavePres(this.binding.edtPrescriptionName.getText().toString());

        });
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AddPrescriptionViewModel.class);
        // Gán ViewModel cho DataBinding
        this.binding.setViewModel(viewModel);
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void observeError() {
        this.viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                if(viewModel.isNameEmpty()) {
                    this.binding.txtErrorName.setVisibility(View.VISIBLE);
                    this.binding.edtPrescriptionName.getBackground().setState(new int[]{R.attr.state_error});
                } else {
                    showErrorDialog("Vui lòng chọn ít nhất một thuốc");
                }
            } else {
                this.binding.txtErrorName.setVisibility(View.GONE);
            }
        });
    }

    public void showErrorDialog(String errorMessage) {
        // Tạo View từ layout đã định nghĩa
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_error, null);

        // Lấy các view cần thiết từ dialogView
        TextView errorMessageTextView = dialogView.findViewById(R.id.tv_error_message);
        errorMessageTextView.setText(errorMessage);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView) // Đặt custom layout vào AlertDialog
                .setCancelable(false) // Không cho phép đóng ngoài các nút
                .create();

        // Hiển thị AlertDialog
        alertDialog.show();

        // Thiết lập vị trí gần sát phía trên màn hình
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.TOP); // Đặt vị trí của dialog gần phía trên
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0f);

            // Set vị trí cách top 80px
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = 80;
            window.setAttributes(layoutParams);
            // Lấy chiều cao của màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            // Thêm animation cho việc trượt xuống
            TranslateAnimation slideDown = new TranslateAnimation(0, 0, -screenHeight , 0);
            slideDown.setDuration(200); // Thời gian trượt xuống
            dialogView.startAnimation(slideDown);
        }

        // Dùng Handler để tự động đóng AlertDialog sau 3 giây và thêm animation trượt lên
        new Handler().postDelayed(() -> {
            // Animation trượt lên
            TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, -alertDialog.getWindow().getDecorView().getHeight());
            slideUp.setDuration(500); // Thời gian trượt lên
            dialogView.startAnimation(slideUp);

            // Đóng dialog sau khi animation hoàn tất
            new Handler().postDelayed(alertDialog::dismiss, 500); // Đợi cho animation hoàn tất
        }, 3000); // 3 giây hiển thị trước khi đóng
    }


}