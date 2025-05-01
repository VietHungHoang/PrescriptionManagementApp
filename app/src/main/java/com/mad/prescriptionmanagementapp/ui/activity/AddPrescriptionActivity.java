package com.mad.prescriptionmanagementapp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.ActivityAddPrescriptionBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.addprescription.AddPrescriptionInfoFragment;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

public class AddPrescriptionActivity extends AppCompatActivity {
    private ActivityAddPrescriptionBinding binding;
    private AddPrescriptionViewModel viewModel;

    private final AddPrescriptionInfoFragment initialFragment = new AddPrescriptionInfoFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();
        this.loadInitialFragment();

        this.binding.btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());


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

    public void setCustomTitle(String title) {
        this.binding.txtTitle.setText(title);
    }
}
