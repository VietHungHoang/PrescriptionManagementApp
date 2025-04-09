package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.CustomerRegisterGoogleRequest;
import com.mad.prescriptionmanagementapp.databinding.ActivitySelectBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.AddCustomerInfoFragment;
import com.mad.prescriptionmanagementapp.ui.fragment.FragmentType;
import com.mad.prescriptionmanagementapp.ui.fragment.SelectUserTypeFragment;
import com.mad.prescriptionmanagementapp.ui.viewmodel.LoginViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.UserDataCollectionViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SelectActivity extends AppCompatActivity implements FragmentInteractionListener {
    public static final String EXTRA_FRAGMENT_TYPE = "SelectActivity.EXTRA_FRAGMENT_TYPE";
    private ActivitySelectBinding binding;
    private UserDataCollectionViewModel dataViewModel;
    private LoginViewModel loginViewModel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Setup Data Binding
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_select);
        this.dataViewModel = new ViewModelProvider(this).get(UserDataCollectionViewModel.class);
        this.loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        this.intent = this.getIntent();


        // Init loginViewModel if needed
        // loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnBack.setOnClickListener(v -> {
            FragmentManager fragmentManager = this.getSupportFragmentManager();

            // Kiểm tra xem có gì trong back stack không (tùy chọn nhưng an toàn)
            if (fragmentManager.getBackStackEntryCount() > 1) {
                // Thực hiện hành động tương tự nút Back của hệ thống: pop back stack
                fragmentManager.popBackStack();
            } else {
                // Tùy chọn: Xử lý trường hợp không có gì để pop
                // Ví dụ: có thể đóng Activity nếu đây là fragment cuối cùng
                this.finish();
            }

            // Cách gọi đơn giản nhất (thường đủ dùng nếu bạn chắc chắn fragment này nằm trên back stack):
            // getParentFragmentManager().popBackStack();
        });
        binding.btnConfirm.setOnClickListener(v -> handleConfirmBtn());
        this.loadInitialFragmentFromIntent();
    }

    private void loadInitialFragmentFromIntent() {
        Intent intent = getIntent();
        FragmentType initialType = FragmentType.UNKNOWN; // Giá trị mặc định

        if (intent != null && intent.hasExtra(EXTRA_FRAGMENT_TYPE)) {
            String typeName = intent.getStringExtra(EXTRA_FRAGMENT_TYPE);
            try {
                initialType = FragmentType.valueOf(typeName); // Chuyển String thành Enum
            } catch (IllegalArgumentException | NullPointerException e) {
                Log.e("SelectActivity", "Invalid or null fragment type received: " + typeName, e);
                // Giữ nguyên initialType = FragmentType.UNKNOWN hoặc xử lý lỗi khác
            }
        } else {
            Log.w("SelectActivity", "Fragment type not provided in Intent. Check calling Activity.");
            // Giữ nguyên initialType = FragmentType.UNKNOWN hoặc đặt một mặc định an toàn
            // initialType = FragmentType.SELECT_USER_TYPE; // Ví dụ đặt mặc định
        }

        Fragment fragmentToLoad;

        switch (initialType) {
            case SELECT_USER_TYPE:

                fragmentToLoad = new SelectUserTypeFragment();

                // Có thể truyền dữ liệu từ Intent của Activity vào Fragment nếu cần
                // Bundle args = new Bundle();
                // args.putString("some_data", intent.getStringExtra("SOME_DATA_KEY"));
                // fragmentToLoad.setArguments(args);
                break;
            // case PROFILE_SETUP:
            //     fragmentToLoad = new ProfileSetupFragment();
            //     break;
            // case TERMS_CONDITIONS:
            //     fragmentToLoad = new TermsConditionsFragment();
            //     break;
            case UNKNOWN:
            default:
                // Xử lý trường hợp không xác định hoặc lỗi
                Log.e("SelectActivity", "Cannot determine initial fragment. Loading default or showing error.");
                // Có thể hiển thị một Fragment báo lỗi hoặc load một Fragment mặc định an toàn
                fragmentToLoad = new SelectUserTypeFragment(); // Ví dụ: Quay lại SelectUserType
                // Hoặc finish(); // Hoặc đóng activity nếu không thể tiếp tục
                break;
        }

        if (fragmentToLoad != null) {
            loadFragment(fragmentToLoad);
        }
    }

    // Phương thức để load Fragment vào container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void handleConfirmBtn() {
        // Get current Fragment displayed in the container
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(binding.fragmentContainer.getId());

        if (currentFragment instanceof SelectUserTypeFragment) {
            Fragment newFragment = new AddCustomerInfoFragment();
            String name = this.intent.getStringExtra("fullName");
            this.dataViewModel.setName(name);
            this.loadFragment(newFragment);

        } else {
            if (this.binding.btnConfirm.getText().equals("Hoàn tất")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String date = this.dataViewModel.getDob().getValue();
                LocalDate dobDate = null;
                if (date != null && !date.equals("")) {
                    dobDate = LocalDate.parse(date, formatter);
                }

                if (this.dataViewModel.getRoleId().getValue() == 1) {
                    CustomerRegisterGoogleRequest customer = CustomerRegisterGoogleRequest.builder()
                            .googleAccountId(this.intent.getStringExtra("googleAccountId"))
                            .name(this.dataViewModel.getName().getValue())
                            .dob(dobDate)
                            .phoneNumber(this.dataViewModel.getPhoneNumber().getValue())
                            .countryId(this.dataViewModel.getCountryId().getValue())
                            .roleId(this.dataViewModel.getRoleId().getValue())
                            .gender(this.dataViewModel.getGender().getValue())
                            .email(this.intent.getStringExtra("email"))
                            .build();
                    this.loginViewModel.registerWithGoogle(this, customer);
                }
            }
        }
        // Thêm else if cho các loại Fragment khác nếu cần
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void setToolbarTitle(String title) {
        binding.txtTitle.setText(title);
    }

    @Override
    public void enableContinueButton(boolean enabled, String text) {
        binding.btnConfirm.setEnabled(enabled);
        // Có thể thay đổi style nút khi bị vô hiệu hóa (ví dụ: alpha)
        binding.btnConfirm.setAlpha(enabled ? 1.0f : 0.5f);
        binding.btnConfirm.setText(text);
    }
}