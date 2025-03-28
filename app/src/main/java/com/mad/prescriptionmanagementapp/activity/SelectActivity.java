package com.mad.prescriptionmanagementapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.fragment.SelectUserTypeFragment;

public class SelectActivity extends AppCompatActivity implements SelectUserTypeFragment.OnUserTypeSelectedListener {
    private ImageButton btnBack;
    private TextView txtTitle;
    private AppCompatButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        EdgeToEdge.enable(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.btnBack = this.findViewById(R.id.btnBack);
        this.txtTitle = this.findViewById(R.id.txtTitle);
        this.btnConfirm = findViewById(R.id.btnConfirm);

        String fragmentType = getIntent().getStringExtra("FRAGMENT_TYPE");

        Fragment fragment = null;
        switch (fragmentType) {
            case "SELECT_USER_TYPE":
                this.txtTitle.setText("Chọn loại người dùng");
                this.btnConfirm.setText("Tiếp tục");
                fragment = new SelectUserTypeFragment();
                break;
        }
        if(fragment != null) {
            this.loadFragment(fragment);
        }
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setAlpha(0.5f);
        btnConfirm.setEnabled(false);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onUserTypeSelected(boolean isSelected) {
        if (isSelected) {
            btnConfirm.setAlpha(1f);
            btnConfirm.setEnabled(true);
        }
    }
}