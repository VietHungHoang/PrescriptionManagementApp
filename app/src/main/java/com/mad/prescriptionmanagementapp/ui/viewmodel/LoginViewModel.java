package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CancellationSignal;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.mad.prescriptionmanagementapp.data.model.User;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.CustomerRegisterGoogleRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.LoginResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.GoogleAuthRespone;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.repository.LoginRepository;
import com.mad.prescriptionmanagementapp.reminder.ReminderWorker;
import com.mad.prescriptionmanagementapp.util.AuthStatus;
import com.mad.prescriptionmanagementapp.util.Constants;
import com.mad.prescriptionmanagementapp.util.Event;
import com.mad.prescriptionmanagementapp.util.Resource;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private final LoginRepository loginRepository;
    private final SharedPreferences sharedPreferences;

    // Declare variables for Google authentication (get idToken)
    private final Executor mainExecutor;
    private final CredentialManager credentialManager;
    private CancellationSignal cancellationSignal;

    // LiveData for input data
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");

    // LiveData for error state
    private final MutableLiveData<String> emailError = new MutableLiveData<>(null);
    private final MutableLiveData<String> passwordError = new MutableLiveData<>(null);

    // Livedata for overall login and user information
    private final MutableLiveData<AuthStatus> _authStatus = new MutableLiveData<>(AuthStatus.IDLE);
    public final LiveData<AuthStatus> authStatus = _authStatus;

    // Livedata for the specific state of taking credential from Google
    private final MutableLiveData<Resource<String>> _googleSignInStatus = new MutableLiveData<>();
    public final LiveData<Resource<String>> googleSignInStatus = _googleSignInStatus;


    // LiveData for error messages (can be shared or separate)
    private final MutableLiveData<Event<String>> _errorMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> errorMessage = _errorMessage;

    // LiveData for navigation events that need to be registered
    private final MutableLiveData<Event<GoogleAuthRespone>> _navigateToUserInfo = new MutableLiveData<>();
    public final LiveData<Event<GoogleAuthRespone>> navigateToUserInfo = _navigateToUserInfo;

    // LiveData for login/registration success navigation event -> Main screen
    private final MutableLiveData<Event<Boolean>> _navigateToMain = new MutableLiveData<>();
    public final LiveData<Event<Boolean>> navigateToMain = _navigateToMain;

    // Temporarily store idToken when transitioning to the AddUserInfo screen
    private String pendingIdToken = null;

    // SharedPreferences Keys
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.loginRepository = new LoginRepository(); // Consider upgrading to use DI
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mainExecutor = ContextCompat.getMainExecutor(application);
        this.credentialManager = CredentialManager.create(application);
    }

    public void onClickBtnLogin(View view) {
        String currentEmail = this.email.getValue() != null ? this.email.getValue().trim() : "";
        String currentPassword = this.password.getValue() != null ? this.password.getValue() : ""; // Password is usually not trim()
        boolean isValid = this.isInputValid(currentEmail, currentPassword);
        // Updating......
    }

    private boolean isInputValid(String email, String password) {
        boolean isValid = true;

        // Validate Email
        if (email.isEmpty()) {
            this.emailError.setValue("Email không được để trống");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailError.setValue("Định dạng email không hợp lệ");
            isValid = false;
        } else {
            this.emailError.setValue(null);
        }

        // Validate Password
        if (password.isEmpty()) {
            this.passwordError.setValue("Mật khẩu không được để trống");
            isValid = false;
        } else {
            this.passwordError.setValue(null);
        }
        return isValid;
    }

    public void handle() {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(100, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(this.getApplication()).enqueue(request);
    }



    // Start the Google login flow
    public void startGoogleSignIn(View view) { // Context is required to call getCredentialAsync
        this._authStatus.setValue(AuthStatus.GOOGLE_LOADING);
        this.pendingIdToken = null; // Reset old token

        if (this.cancellationSignal != null && !this.cancellationSignal.isCanceled()) {
            this.cancellationSignal.cancel();
        }
        this.cancellationSignal = new CancellationSignal();

        String nonce = generateNonce();

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constants.GOOGLE_WEB_CLIENT_ID)
                .setNonce(nonce)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        this.credentialManager.getCredentialAsync(
                view.getContext(), // Context from Activity/Fragment
                request,
                cancellationSignal,
                mainExecutor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Log.i(Constants.TAG, "Google Credential received.");
                        LoginViewModel.this._authStatus.setValue(AuthStatus.GOOGLE_SUCCESS);
                        LoginViewModel.this.handleGoogleCredential(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(Constants.TAG, "GetCredentialException", e);
                        LoginViewModel.this._authStatus.setValue(AuthStatus.GOOGLE_FAILED);
                        LoginViewModel.this._errorMessage.setValue(new Event<>("Get information from Google error: " + e.getMessage()));
                    }
                }
        );
    }

    private void handleGoogleCredential(Credential credential) {
        if (credential instanceof GoogleIdTokenCredential) {
            GoogleIdTokenCredential googleIdCredential = (GoogleIdTokenCredential) credential;
            this.pendingIdToken = googleIdCredential.getIdToken();
            this.verifyTokenWithBackend(this.pendingIdToken);
        } else {
            this._authStatus.setValue(AuthStatus.GOOGLE_FAILED);
            this._errorMessage.setValue(new Event<>("Unexpected Google information"));
        }
    }


    private void verifyTokenWithBackend(String idToken) {
        if (idToken == null) return;
        this._authStatus.setValue(AuthStatus.VERIFYING);
        LoginViewModel self = LoginViewModel.this;
        this.loginRepository.loginGoogle(idToken, new Callback<GoogleAuthRespone>() {
            @Override
            public void onResponse(@NonNull Call<GoogleAuthRespone> call, @NonNull Response<GoogleAuthRespone> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GoogleAuthRespone verifyResponse = response.body();
                    Log.i(Constants.TAG, "Backend verify response: status=" + verifyResponse.getStatus());
                    if (verifyResponse.getStatus().equals("LOGIN_SUCCESS")) {
                        self.saveUserSession(verifyResponse.getToken(), null);
                        self._authStatus.setValue(AuthStatus.LOGIN_SUCCESS);
                        self._navigateToMain.setValue(new Event<>(true)); // Navigate to app
                        pendingIdToken = null; // Delete temporary token

                    } else if ("REGISTRATION_REQUIRED".equals(verifyResponse.getStatus())) {
                        self._authStatus.setValue(AuthStatus.REGISTRATION_REQUIRED);
                        // Send necessary information vie Event
                        self._navigateToUserInfo.setValue(new Event<>(verifyResponse));

                    } else {
                        Log.e(Constants.TAG, "Unexpected status from verify API: " + verifyResponse.getStatus());
                        self._authStatus.setValue(AuthStatus.VERIFY_FAILED);
                        self._errorMessage.setValue(new Event<>("Invalid response from server"));
                        self.pendingIdToken = null;
                    }
                } else {
                    self.handleApiError(response, AuthStatus.VERIFY_FAILED, "Authentication error with server");
                    self.pendingIdToken = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleAuthRespone> call, @NonNull Throwable t) {
                Log.e(Constants.TAG, "Verify API call failed", t);
                self._authStatus.setValue(AuthStatus.VERIFY_FAILED);
                self._errorMessage.setValue(new Event<>("Lỗi mạng hoặc kết nối: " + t.getMessage()));
                self.pendingIdToken = null;
            }
        });
    }

    private void saveUserSession(String token, @Nullable User user) {
        if (token == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.apply();
        Log.i(Constants.TAG, "User session saved (JWT Token).");
    }

    private String generateNonce(int length) {
        if (length <= 0) length = 16;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder nonce = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            nonce.append(chars.charAt(random.nextInt(chars.length())));
        }
        return nonce.toString();
        // Other: return UUID.randomUUID().toString();
    }

    private String generateNonce() {
        return this.generateNonce(16);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    private <T> void handleApiError(Response<T> response, AuthStatus errorStatus, String defaultMessage) {
        String errorMsg = defaultMessage;
        if (response.errorBody() != null) {
            try {
                String errorBodyStr = response.errorBody().string();
                errorMsg += ": " + response.code() + " " + errorBodyStr;
                Log.e(Constants.TAG, "API Error Body: " + errorBodyStr);
            } catch (IOException e) {
                errorMsg += ": " + response.code() + " (Cannot read error body)";
                Log.e(Constants.TAG, "IOException reading error body", e);
            } catch (Exception e) { // Catch parsing errors too
                errorMsg += ": " + response.code() + " (Error processing error body)";
                Log.e(Constants.TAG, "Exception processing error body", e);
            }
        } else {
            errorMsg += ": " + response.code();
        }
        this._authStatus.setValue(errorStatus);
        this._errorMessage.setValue(new Event<>(errorMsg));
    }

    @Nullable
    public String getPendingIdToken() {
        return this.pendingIdToken;
    }

    public void registerWithGoogle(Context context, CustomerRegisterGoogleRequest request) {
        final LoginViewModel self = LoginViewModel.this;
        this.loginRepository.registerGoogle(request, new Callback<ResponseObject<LoginResponse>>() {
            @Override
            public void onResponse(Call<ResponseObject<LoginResponse>> call, Response<ResponseObject<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body().getData();
                    SharedPreferences.Editor editor = self.sharedPreferences.edit();
                    editor.putString("JWT_TOKEN", loginResponse.getToken());
                    editor.putString("REFRESH_TOKEN", loginResponse.getRefreshToken());
                    editor.apply();
                    new AlertDialog.Builder(context)
                            .setTitle("Đăng ký thành công")
                            .setMessage("Bạn đã đăng ký tài khoản thành công!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Hanle click to "OK" to navigate to Home Screen
                                dialog.dismiss();
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<LoginResponse>> call, Throwable throwable) {

            }
        });
    }
}