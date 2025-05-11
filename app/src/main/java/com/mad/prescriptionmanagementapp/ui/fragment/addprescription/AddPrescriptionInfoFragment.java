package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.SelectedDrugAdapter;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ErrorDialog;
import com.mad.prescriptionmanagementapp.ui.listener.OnSelectedDrugClickListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionInfoViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AddPrescriptionInfoFragment extends Fragment implements OnSelectedDrugClickListener {
    private FragmentAddPrescriptionInfoBinding binding;
    private AddPrescriptionViewModel shareViewModel;
    private AddPrescriptionInfoViewModel viewModel;

    public static AddPrescriptionInfoFragment newInstance() {
        return new AddPrescriptionInfoFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddPrescriptionInfoBinding.inflate(inflater, container, false);
        this.initViewModel();
        return this.binding.getRoot();
    }

    private void initViewModel() {
        this.shareViewModel = new ViewModelProvider(this.requireActivity()).get(AddPrescriptionViewModel.class);
        this.viewModel = new ViewModelProvider(this).get(AddPrescriptionInfoViewModel.class);
        this.binding.setViewModel(shareViewModel);
        this.binding.setLifecycleOwner(this.getViewLifecycleOwner());
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AddPrescriptionActivity) requireActivity()).setCustomTitle("Thêm đơn thuốc");
        this.enableMedicalInfo(this.binding.switchMedicalInfo.isChecked());
        this.setupUI();
        this.setOnclickView();
        this.setOnFocusEditText();
        this.observeError();
        this.setAdapter();
    }

    private void setupUI() {
        PrescriptionRequest pres = this.shareViewModel.getPrescription().getValue();
        this.binding.switchMedicalInfo.setChecked(this.shareViewModel.isOnMedicalInfo().getValue());
        this.binding.edtPrescriptionName.setText(pres.getName());
        this.binding.edtHospital.setText(pres.getHospital());
        this.binding.edtDoctor.setText(pres.getDoctorName());
        this.binding.edtConsultationDate.setText(pres.getConsultationDate());
        this.binding.edtFollowUpDate.setText(pres.getFollowUpDate());
    }

    private void setOnclickView() {
        this.binding.btnAddDrug.setOnClickListener(v -> {
            Tools.changeFragment(this, new SelectDrugFragment());
        });

        this.binding.btnSave.setOnClickListener(v -> {
            this.updatePres();
            this.shareViewModel.handleBtnSavePres(this.requireContext());
        });

        this.binding.edtConsultationDate.setOnClickListener(v -> {
            this.showDatePickerDialog(this.binding.edtConsultationDate);
        });

        this.binding.edtFollowUpDate.setOnClickListener(v -> {
            this.showDatePickerDialog(this.binding.edtFollowUpDate);
        });

        this.binding.switchMedicalInfo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.enableMedicalInfo(this.binding.switchMedicalInfo.isChecked());
        });
    }

    private void setOnFocusEditText() {
        this.binding.edtPrescriptionName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                this.shareViewModel.resetErrorMessage();
            }
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addPresName(text);
            }
        });

        this.binding.edtHospital.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addHospital(text);
            }
        });

        this.binding.edtDoctor.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addDoctor(text);
            }
        });

        this.binding.edtConsultationDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addConsultionDate(text);
            }
        });

        this.binding.edtFollowUpDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                this.shareViewModel.addFollowUpDate(text);
            }
        });
    }

    private void setAdapter() {
        SelectedDrugAdapter selectedDrugAdapter = new SelectedDrugAdapter(this.shareViewModel.getSelectedDrugs().getValue(), this);
        this.binding.rcvDrugInfo.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.binding.rcvDrugInfo.setAdapter(selectedDrugAdapter);
    }

    private void enableMedicalInfo(boolean isEnabled) {
        this.binding.cardMedicalInfo.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    private void observeError() {
        this.shareViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                if (shareViewModel.isNameEmpty()) {
                    this.binding.txtErrorName.setVisibility(View.VISIBLE);
                    this.binding.edtPrescriptionName.getBackground().setState(new int[]{R.attr.state_error});
                } else {
                    new ErrorDialog(this, "Vui lòng chọn ít nhất một thuốc", true).showDialog();
                }
            } else {
                this.binding.txtErrorName.setVisibility(View.GONE);
            }
        });
    }


//    public void showErrorDialog(String errorMessage) {
//        new ErrorDialog(this, "V")
//        // Tạo View từ layout đã định nghĩa
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_error, null);
//
//        // Lấy các view cần thiết từ dialogView
//        TextView errorMessageTextView = dialogView.findViewById(R.id.tv_error_message);
//        errorMessageTextView.setText(errorMessage);
//
//        // Tạo AlertDialog
//        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
//                .setView(dialogView) // Đặt custom layout vào AlertDialog
//                .setCancelable(false) // Không cho phép đóng ngoài các nút
//                .create();
//
//        // Hiển thị AlertDialog
//        alertDialog.show();
//
//        // Thiết lập vị trí gần sát phía trên màn hình
//        Window window = alertDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.setGravity(Gravity.TOP); // Đặt vị trí của dialog gần phía trên
//            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setDimAmount(0f);
//
//            // Set vị trí cách top 80px
//            WindowManager.LayoutParams layoutParams = window.getAttributes();
//            layoutParams.y = 80;
//            window.setAttributes(layoutParams);
//            // Lấy chiều cao của màn hình
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            this.requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int screenHeight = displayMetrics.heightPixels;
//
//            // Thêm animation cho việc trượt xuống
//            TranslateAnimation slideDown = new TranslateAnimation(0, 0, -screenHeight, 0);
//            slideDown.setDuration(200); // Thời gian trượt xuống
//            dialogView.startAnimation(slideDown);
//        }
//
//        // Dùng Handler để tự động đóng AlertDialog sau 3 giây và thêm animation trượt lên
//        new Handler().postDelayed(() -> {
//            // Animation trượt lên
//            TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, -alertDialog.getWindow().getDecorView().getHeight());
//            slideUp.setDuration(500); // Thời gian trượt lên
//            dialogView.startAnimation(slideUp);
//
//            // Đóng dialog sau khi animation hoàn tất
//            new Handler().postDelayed(alertDialog::dismiss, 500); // Đợi cho animation hoàn tất
//        }, 3000); // 3 giây hiển thị trước khi đóng
//    }

private void showDatePickerDialog(EditText editText) {
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
    List<DrugInPres> drugs = this.shareViewModel.getSelectedDrugs().getValue();
    this.shareViewModel.setCurrentDrug(drugs.stream().filter(drug -> drug.getDrug().getId() == drugInPres.getDrug().getId()).findFirst().orElse(null));
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
                    R.anim.fade_out)  // Fragment B ra khi Back (zoom out)
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
    if (this.shareViewModel.getPrescription().getValue() != null) {
        this.shareViewModel.updatePrescription(this.binding.edtPrescriptionName.getText().toString()
                , this.binding.switchMedicalInfo.isChecked()
                , this.binding.edtHospital.getText().toString()
                , this.binding.edtDoctor.getText().toString()
                , this.binding.edtConsultationDate.getText().toString()
                , this.binding.edtFollowUpDate.getText().toString()
        );
    }
}
}