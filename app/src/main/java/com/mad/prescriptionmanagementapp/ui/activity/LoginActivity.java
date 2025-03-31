package com.mad.prescriptionmanagementapp.activity;

import android.content.Intent;
import android.os.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.credentials.*;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.GetCredentialInterruptedException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.mad.prescriptionmanagementapp.R;
import java.security.SecureRandom;
import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MyAPP";

    private EditText edtEmail, edtPassword;
    private TextView txtErrorEmail, txtErrorPassword;

    private AppCompatButton btnLogin, btnGoogleLogin;
    private TextView txtBtnRegister;

    private CredentialManager credentialManager;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private ActivityResultLauncher<IntentSenderRequest> credentialSignInLauncher;

    private Executor mainExecutor; // Executor để chạy callback trên Main thread
    private CancellationSignal cancellationSignal; // Để hủy yêu cầu nếu cần

//    keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);

        // Automatically change the status bar color to match
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        this.credentialManager = CredentialManager.create(this);
        mainExecutor = ContextCompat.getMainExecutor(this);

        // Mapping view
        this.edtEmail = findViewById(R.id.edtEmail);
        this.edtPassword = findViewById(R.id.edtPassword);
        this.txtErrorEmail = findViewById(R.id.txtErrorEmail);
        this.txtErrorPassword = findViewById(R.id.txtErrorPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.txtBtnRegister = findViewById(R.id.txtBtnRegister);
        this.btnGoogleLogin = findViewById(R.id.googleLogin);

        this.btnLogin.setOnClickListener(v -> this.validateLogin());
        this.txtBtnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
            intent.putExtra("FRAGMENT_TYPE", "SELECT_USER_TYPE");
            startActivity(intent);

        });

        this.btnGoogleLogin.setOnClickListener(v -> startGoogleSignIn());
    }

    // Gọi màn hình đăng nhập Google
    private void startGoogleSignIn() {
        // Hủy yêu cầu trước đó nếu đang chạy
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        cancellationSignal = new CancellationSignal(); // Tạo CancellationSignal mới

        String nonce = generateNonce(); // Tạo nonce mới cho mỗi lần yêu cầu
        Log.d(TAG, "Generated Nonce: " + nonce);

        // 1. Tạo tùy chọn GetGoogleIdOption
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Chỉ hiển thị tài khoản đã từng đăng nhập ứng dụng này
                .setServerClientId("689157132294-p268fum6akhfo29qmui996v1ss07oqgi.apps.googleusercontent.com")
                .setNonce(nonce) // Gửi nonce để liên kết với ID token
//                .setAutoSelectEnabled(true) // Cho phép tự động chọn tài khoản (One Tap) nếu có thể
                .build();

        // 2. Tạo GetCredentialRequest
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // 3. Gọi CredentialManager để lấy credential
        // Sử dụng phương thức callback mới (thay thế cho ListenableFuture trong các ví dụ cũ)
        credentialManager.getCredentialAsync(
                this, // Context
                request, // Yêu cầu
                cancellationSignal, // Tín hiệu hủy bỏ (có thể là null)
                mainExecutor, // Executor để chạy callback (dùng main thread để cập nhật UI)
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Xử lý thành công
                        Log.i(TAG, "Got credential response");
                        handleSignInSuccess(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        // Xử lý lỗi
                        handleSignInFailure(e);
                    }
                }
        );
    }

    // Xử lý khi lấy credential thành công
    private void handleSignInSuccess(Credential credential) {
        if (credential instanceof GoogleIdTokenCredential) {
            GoogleIdTokenCredential googleIdCredential = (GoogleIdTokenCredential) credential;
            String idToken = googleIdCredential.getIdToken();
            String displayName = googleIdCredential.getDisplayName();
            String email = googleIdCredential.getId(); // ID thường là email

            Log.i(TAG, "Google Sign-In successful!");
            Log.d(TAG, "ID Token: " + idToken.substring(0, Math.min(idToken.length(), 20)) + "..."); // Log một phần token
            Log.d(TAG, "Display Name: " + displayName);
            Log.d(TAG, "Email/ID: " + email);
            Log.d(TAG, "Given Name: " + googleIdCredential.getGivenName());
            Log.d(TAG, "Family Name: " + googleIdCredential.getFamilyName());
            Log.d(TAG, "Profile Picture URI: " + googleIdCredential.getProfilePictureUri());

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // *** GỬI ID TOKEN TỚI SERVER CỦA BẠN ĐỂ XÁC THỰC ***
            // sendIdTokenToServer(idToken);

            // Bạn cũng có thể thử phân tích token phía client (chủ yếu để debug)
            // nhưng việc xác thực cuối cùng phải diễn ra ở backend.
            try {
                // GoogleIdToken googleIdToken = GoogleIdToken.parse(googleIdCredential.getIdToken());
                // Log.d(TAG, "Nonce from parsed token: " + googleIdToken.getPayload().getNonce());
                // Log.d(TAG, "Token expires at: " + googleIdToken.getPayload().getExpirationTimeSeconds());
                // Lưu ý: Việc parse token client-side cần thêm thư viện JWT hoặc Google API Client Library for Java
                Log.d(TAG,"ID token received. Send to backend for verification.");
            } catch (Exception e) { // Bắt ngoại lệ chung nếu không có thư viện parse
                Log.e(TAG, "Could not parse ID token on client (or library not included)", e);
            }

        } else {
            // Xử lý các loại credential khác nếu cần (PasswordCredential, PublicKeyCredential)
            Log.w(TAG, "Received unexpected credential type: " + credential.getClass().getName());
        }
    }

    // Xử lý khi lấy credential thất bại
    private void handleSignInFailure(@NonNull GetCredentialException e) {
        Log.e(TAG, "Google Sign-In failed", e);

        String errorMessage = "Lỗi không xác định";
        if (e instanceof NoCredentialException) {
            errorMessage = "Không tìm thấy tài khoản Google nào trên thiết bị hoặc người dùng chưa chọn.";
            // Có thể gợi ý người dùng thêm tài khoản Google vào thiết bị
        } else if (e instanceof GetCredentialCancellationException) {
            errorMessage = "Người dùng đã hủy đăng nhập.";
        } else if (e instanceof GetCredentialInterruptedException) {
            errorMessage = "Quá trình đăng nhập bị gián đoạn.";
            // Thường do Activity bị hủy hoặc có sự kiện hệ thống khác
        } else if (e instanceof GetPublicKeyCredentialDomException) {
            // Lỗi liên quan đến WebAuthn/Passkey, ít xảy ra với Google ID
            errorMessage = "Lỗi Passkey/WebAuthn: " + e.getMessage();
        } else {
            errorMessage = "Lỗi đăng nhập: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }

        Log.e(TAG, errorMessage, e); // Log chi tiết lỗi
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Tạo một giá trị ngẫu nhiên dùng một lần (nonce).
     * Trong ứng dụng thực tế, nonce nên được tạo bởi server của bạn,
     * gửi tới client, client gửi lại cùng yêu cầu đăng nhập,
     * và server kiểm tra nonce này trong ID token nhận được để chống tấn công phát lại.
     *
     * @param length Độ dài mong muốn của nonce (mặc định 16)
     * @return Chuỗi nonce ngẫu nhiên
     */
    private String generateNonce(int length) {
        if (length <= 0) {
            length = 16; // Độ dài mặc định
        }
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder nonce = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            nonce.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return nonce.toString();
        // Hoặc đơn giản hơn cho demo: return UUID.randomUUID().toString();
    }

    // Quá tải phương thức để gọi với độ dài mặc định
    private String generateNonce() {
        return generateNonce(16);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bỏ yêu cầu nếu Activity bị hủy
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    private void validateLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean isValid = true;

        if (email.isEmpty()) {
            this.edtEmail.setBackgroundResource(R.drawable.edittext_bg_error);
            this.txtErrorEmail.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            this.edtEmail.setBackgroundResource(R.drawable.edittext_bg_normal);
            this.txtErrorEmail.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            this.edtPassword.setBackgroundResource(R.drawable.edittext_bg_error);
            this.txtErrorPassword.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            this.edtPassword.setBackgroundResource(R.drawable.edittext_bg_normal);
            this.txtErrorPassword.setVisibility(View.GONE);
        }

        if (isValid) {

        }
    }
}
