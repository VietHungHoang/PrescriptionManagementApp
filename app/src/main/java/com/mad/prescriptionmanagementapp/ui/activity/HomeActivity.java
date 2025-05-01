package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.ActivityHomeBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.HomeViewModel;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void initParam() {
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
//        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.binding = ActivityHomeBinding.inflate(this.getLayoutInflater());
        this.setContentView(this.binding.getRoot());
        this.viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        this.binding.setViewModel(this.viewModel);
        this.binding.setLifecycleOwner(this);

        this.binding.fab.setOnClickListener(v -> showOptions());

    }

    private void showOptions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_add_options, null);

        sheetView.findViewById(R.id.option_add_prescription).setOnClickListener(v -> {
//            viewModel.onAddPrescriptionClicked();
            this.startActivity(new Intent(this, AddPrescriptionActivity.class));
            dialog.dismiss();
        });

        sheetView.findViewById(R.id.option_add_schedule).setOnClickListener(v -> {
            viewModel.onAddScheduleClicked();
            dialog.dismiss();
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }
}