package com.mad.prescriptionmanagementapp.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.ActivityAddPrescriptionBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.FragmentType;
import com.mad.prescriptionmanagementapp.ui.fragment.addprescription.AddPrescriptionInfoFragment;
import com.mad.prescriptionmanagementapp.ui.fragment.addprescription.AddScheduleFragment;
import com.mad.prescriptionmanagementapp.ui.fragment.dialog.ConfirmDialog;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddScheduleViewModel;
import com.mad.prescriptionmanagementapp.util.FragmentName;

public class AddPrescriptionActivity extends AppCompatActivity {
    private ActivityAddPrescriptionBinding binding;
    private AddPrescriptionViewModel viewModel;

    private final AddPrescriptionInfoFragment initialFragment = new AddPrescriptionInfoFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();
        this.loadInitialFragment();
//        new ViewModelProvider(this).get(AddScheduleViewModel.class);

//        this.binding.btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
//// Đăng ký callback để xử lý back button
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//                if (currentFragment == null || isFirstFragment() || currentFragment instanceof AddPrescriptionInfoFragment) {
//                    if (currentFragment instanceof AddPrescriptionInfoFragment && viewModel.existedPres()) {
//                            ConfirmDialog.showCancelConfirmationDialog(AddPrescriptionActivity.this, new ConfirmDialog.ConfirmationDialogListener() {
//                                @Override
//                                public void onConfirm() {
//                                    finish();
//                                }
//                            }, "Huỷ", "Tất cả thông tin bạn đã nhập sẽ bị xoá, đồng ý huỷ", "Đồng ý");
//                    }
//                    // Không còn fragment, gọi dispatcher để đóng Activity
//                    else {
//                        finish();
//                    }
//                } else {
//                        getSupportFragmentManager().popBackStack();
//                }
//            }
//        });

    }

    private boolean isFirstFragment() {
        return this
                .getSupportFragmentManager()
                .getBackStackEntryCount() == 0;
    }


    private void initParam() {
        EdgeToEdge.enable(this);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_add_prescription);
        this.viewModel = new ViewModelProvider(this).get(AddPrescriptionViewModel.class);
        this.binding.setViewModel(this.viewModel);
        this.binding.setLifecycleOwner(this);
    }

    private void loadInitialFragment() {
        this.loadFragment(this.initialFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
