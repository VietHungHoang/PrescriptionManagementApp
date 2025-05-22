package com.mad.prescriptionmanagementapp.ui.activity.hung;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.databinding.ActivityAddPrescriptionBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.addprescription.AddPrescriptionInfoFragment;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

public class AddPrescriptionActivity extends AppCompatActivity {
    private ActivityAddPrescriptionBinding binding;
    private AddPrescriptionViewModel viewModel;

    private Prescription intentPres;

    private final AddPrescriptionInfoFragment initialFragment = new AddPrescriptionInfoFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();
        this.loadInitialFragment();
        intentPres =  this.getIntent().getParcelableExtra("edit");
        if(intentPres != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isUpdate", true);
            initialFragment.setArguments(bundle);
        }
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

        this.viewModel.setPresFromIntent(this.intentPres);
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
