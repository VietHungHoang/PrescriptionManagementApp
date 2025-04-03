package com.mad.prescriptionmanagementapp.ui.activity;

import android.content.Intent;
import android.os.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.GoogleAuthRespone;
import com.mad.prescriptionmanagementapp.databinding.ActivityLoginBinding;
import com.mad.prescriptionmanagementapp.ui.fragment.FragmentType;
import com.mad.prescriptionmanagementapp.ui.viewmodel.LoginViewModel;
import com.mad.prescriptionmanagementapp.util.AuthStatus;
import com.mad.prescriptionmanagementapp.util.Constants;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

//    keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();
        this.setupObservers();

        this.btnLogin.setOnClickListener(v -> this.validateLogin());
        this.txtBtnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
            intent.putExtra("FRAGMENT_TYPE", "SELECT_USER_TYPE");
            startActivity(intent);

        });

        this.progressBar = findViewById(R.id.progressBar); // Thêm ProgressBar vào layout nếu muốn


        this.btnGoogleLogin.setOnClickListener(v -> {
            Log.d(Constants.TAG, "Sign-in button clicked.");
            // Truyền context vào ViewModel khi bắt đầu sign-in
            viewModel.startGoogleSignIn(this);
        });
    }

    private void initParam() {
        EdgeToEdge.enable(this);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        this.viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        this.binding.setViewModel(this.viewModel);
        this.binding.setLifecycleOwner(this);
    }


    private void setupObservers() {
        // Quan sát trạng thái xác thực
        this.viewModel.authStatus.observe(this, status -> {
            Log.d(Constants.TAG, "Auth Status Changed: " + status);
            updateUIBasedOnStatus(status);
        });

        // Quan sát thông báo lỗi
        this.viewModel.errorMessage.observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        // Quan sát sự kiện chuyển sang màn hình UserInfo
        this.viewModel.navigateToUserInfo.observe(this, event -> {
            GoogleAuthRespone verifyResponse = event.getContentIfNotHandled();
            if (verifyResponse != null) {
                String pendingToken = this.viewModel.getPendingIdToken(); // Lấy token đã lưu tạm
                if (pendingToken == null) {
                    Log.e(Constants.TAG, "Cannot navigate to UserInfo, pending token is missing!");
                    Toast.makeText(this, "Lỗi nội bộ, không thể tiếp tục đăng ký.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(Constants.TAG, "Navigating to UserInfoActivity...");
                Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                intent.putExtra("GOOGLE_ID_TOKEN", pendingToken); // *** Quan trọng: Truyền idToken ***
                intent.putExtra("email", verifyResponse.getEmail());
                intent.putExtra("fullName", verifyResponse.getName());
                intent.putExtra("googleAccountId", verifyResponse.getGoogleAccountId());
                intent.putExtra(SelectActivity.EXTRA_FRAGMENT_TYPE, FragmentType.SELECT_USER_TYPE.name());
//                Lưu ý: Truyền enum.name() (là một String) vì Intent không trực tiếp hỗ trợ truyền enum một cách đơn giản và an toàn trên mọi phiên bản Android (mặc dù có thể serialize nhưng phức tạp hơn).
                startActivity(intent);
                // Không finish() LoginActivity ở đây, để người dùng có thể quay lại nếu cần
            }
        });

        // Quan sát sự kiện chuyển sang màn hình chính
        this.viewModel.navigateToMain.observe(this, event -> {
            Boolean shouldNavigate = event.getContentIfNotHandled();
            if (shouldNavigate != null && shouldNavigate) {
                Log.i(Constants.TAG, "Navigating to MainActivity...");
                // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa stack cũ
                // startActivity(intent);
                // finish(); // Đóng LoginActivity

                Toast.makeText(this, "Đăng nhập/Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // THAY BẰNG CODE CHUYỂN SANG MAIN ACTIVITY CỦA BẠN
            }
        });
    }

    private void updateUIBasedOnStatus(AuthStatus status) {
        switch (status) {
            case IDLE:
            case GOOGLE_SUCCESS:
            case LOGIN_SUCCESS:
            case REGISTRATION_SUCCESS:
            case GOOGLE_FAILED:
            case VERIFY_FAILED:
            case REGISTRATION_FAILED:
//                progressBar.setVisibility(View.GONE);
//                googleSignInButton.setEnabled(true);
                break;
            case GOOGLE_LOADING:
            case VERIFYING:
            case REGISTERING: // Thêm trạng thái này
//                progressBar.setVisibility(View.VISIBLE);
//                googleSignInButton.setEnabled(false);
                break;
            case REGISTRATION_REQUIRED:
                // ProgressBar có thể ẩn hoặc hiện tùy UX bạn muốn
//                progressBar.setVisibility(View.GONE);
//                googleSignInButton.setEnabled(true); // Cho phép thử lại nếu cần
                // UI không đổi nhiều ở màn này, vì điều hướng đã được xử lý
                break;
        }
    }

}



