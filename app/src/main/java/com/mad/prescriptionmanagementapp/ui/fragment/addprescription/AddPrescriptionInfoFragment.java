package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.SelectedDrugAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.listener.OnSelectedDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddPrescriptionInfoFragment extends Fragment implements OnSelectedDrugClickListener {
    private FragmentAddPrescriptionInfoBinding binding;
    private AddPrescriptionViewModel viewModel;

    private SelectedDrugAdapter selectedDrugAdapter;

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
            if (!hasFocus) {
                // Người dùng nhập xong và rời khỏi ô
                String text = ((EditText) v).getText().toString();
                this.viewModel.addPresName(text);
            }
        });

        this.binding.edtHospital.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.viewModel.addHospital(text);
            }
        });

        this.binding.edtDoctor.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.viewModel.addDoctor(text);
            }
        });

        this.binding.edtConsultationDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.viewModel.addConsultionDate(text);
            }
        });

        this.binding.edtFollowUpDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.viewModel.addFollowUpDate(text);
            }
        });
        this.setOnclickDatePicker();
        this.setAdapter();
        this.observeData();
        this.setupUI();
    }

    private void setupUI() {
        PrescriptionRequest pres = this.viewModel.getPrescription().getValue();
        this.binding.switchMedicalInfo.setChecked(this.viewModel.isOnMedicalInfo().getValue());
        this.binding.edtPrescriptionName.setText(pres.getName());
            this.binding.edtHospital.setText(pres.getHospital());
            this.binding.edtDoctor.setText(pres.getDoctorName());
            this.binding.edtConsultationDate.setText(pres.getConsultationDate());
            this.binding.edtFollowUpDate.setText(pres.getFollowUpDate());
    }

    private void observeData() {
    }

    private void setAdapter() {
        this.selectedDrugAdapter = new SelectedDrugAdapter(this.viewModel.getSelectedDrugs().getValue(), this);
        this.binding.rcvDrugInfo.setLayoutManager( new LinearLayoutManager(this.getContext()));
        this.binding.rcvDrugInfo.setAdapter(this.selectedDrugAdapter);
        // Thêm ItemDecoration nếu muốn có đường kẻ phân cách
    }

    private void setOnclickDatePicker() {
        this.binding.edtConsultationDate.setOnClickListener(v -> {
            this.showDatePickerDialog(this.binding.edtConsultationDate);
        });

        this.binding.edtFollowUpDate.setOnClickListener(v -> {
            this.showDatePickerDialog(this.binding.edtFollowUpDate);
        });
    }


    private void setOnclickBtnAddDrug() {

        this.binding.btnAddDrug.setOnClickListener(v -> {
            Fragment newFragment = new SelectDrugFragment();
            this.getParentFragmentManager()
                    .beginTransaction()
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_CLOSE)
                    .setCustomAnimations(
                            R.anim.zoom_in,    // Fragment B vào (zoom in)
                            R.anim.fade_out,   // Fragment A ra (fade out) - fragment cũ
                            R.anim.zoom_out,    // Fragment A vào lại khi Back (fade in) - fragment cũ
                            R.anim.fade_out )  // Fragment B ra khi Back (zoom out)
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
            this.updatePres();
            this.viewModel.handleBtnSavePres(this.requireContext());

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

    public void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(dateStr);
                },
                year, month, day
        );
        datePickerDialog.show();
    }


    @Override
    public void onItemClick(DrugInPres drugInPres) {
        List<DrugInPres> drugs = this.viewModel.getSelectedDrugs().getValue();
        this.viewModel.setCurrentDrug(drugs.stream().filter(drug -> drug.getDrug().getId() == drugInPres.getDrug().getId()).findFirst().orElse(null));
        this.moveToNextFragment(drugInPres.getDrug().getId());
    }

    private void moveToNextFragment(long id) {
        Fragment newFragment = AddScheduleFragment.newInstance(id, true);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.updatePres();
    }

    private void updatePres() {
        if(this.viewModel.getPrescription().getValue() != null) {
            this.viewModel.updatePrescription(this.binding.edtPrescriptionName.getText().toString()
                    ,this.binding.switchMedicalInfo.isChecked()
                    ,this.binding.edtHospital.getText().toString()
                    ,this.binding.edtDoctor.getText().toString()
                    ,this.binding.edtConsultationDate.getText().toString()
                    ,this.binding.edtFollowUpDate.getText().toString()
            );
        }
    }
}