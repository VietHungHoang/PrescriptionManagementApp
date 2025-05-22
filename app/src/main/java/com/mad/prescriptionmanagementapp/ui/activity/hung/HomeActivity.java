package com.mad.prescriptionmanagementapp.ui.activity.hung;

import android.content.Intent;
import androidx.core.graphics.Insets;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.ActivityHomeBinding;
import com.mad.prescriptionmanagementapp.ui.activity.dang.MedicineSearchActivity;
import com.mad.prescriptionmanagementapp.ui.activity.dang.ProfileActivity;
import com.mad.prescriptionmanagementapp.ui.activity.kiet.ListPrescriptionActivity;
import com.mad.prescriptionmanagementapp.ui.activity.kiet.MedicineScheduleActivity;
import com.mad.prescriptionmanagementapp.ui.viewmodel.HomeViewModel;
import com.mad.prescriptionmanagementapp.util.SharedPrefUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private SharedPrefUtils sharedPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();
        this.initViewModel();
        this.setupUI();
        this.setupViewAction();
    }

    private void initParam() {
        EdgeToEdge.enable(this);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.binding = ActivityHomeBinding.inflate(this.getLayoutInflater());
        this.setContentView(this.binding.getRoot());

        this.sharedPrefUtils = new SharedPrefUtils(this);
    }

    private void initViewModel() {
        this.viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        this.binding.setViewModel(this.viewModel);
        this.binding.setLifecycleOwner(this);
    }
    private void setupUI() {
        String name = this.sharedPrefUtils.getName();
        String nameView;
        if(name != null && !name.isEmpty()) {
            nameView = "Chào, " + name;
        }
        else {
            nameView = "Chào, Guest";
        }

        this.binding.tvGreeting.setText(nameView);

        this.binding.tvDate.setText("Hôm nay, " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));

        this.binding.bottomNavigation.setItemBackgroundResource(R.drawable.bottom_nav_background);
    }

    private void setupViewAction() {
        this.binding.fab.setOnClickListener(v ->  {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.dialog_add_options, null);

            sheetView.findViewById(R.id.option_add_prescription).setOnClickListener(u -> {
                this.startActivity(new Intent(this, AddPrescriptionActivity.class));
                dialog.dismiss();
            });

            sheetView.findViewById(R.id.option_add_appointment).setOnClickListener(u -> {
                this.viewModel.onAddScheduleClicked();
                dialog.dismiss();
            });

            dialog.setContentView(sheetView);
            dialog.show();
        });

        binding.cardMedicationInfo.setOnClickListener(v -> openActivity(MedicineSearchActivity.class));
        binding.cardMedicationReminder.setOnClickListener(v -> openActivity(MedicineScheduleActivity.class));
        binding.cardStatistics.setOnClickListener(v -> openActivity(ListPrescriptionActivity.class));

        this.binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int newId = item.getItemId();
            int currentId = binding.bottomNavigation.getSelectedItemId();
            if (newId == currentId) return false; // nếu click đúng tab đang active thì không làm gì

            if (newId == R.id.nav_home) {
                this.startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (newId == R.id.nav_user) {
                this.startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}