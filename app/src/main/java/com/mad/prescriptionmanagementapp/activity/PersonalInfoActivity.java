package com.mad.prescriptionmanagementapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mad.prescriptionmanagementapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etFullName, etDateOfBirth, etPhoneNumber, etEmail;
    private Spinner spinnerCountry, spinnerGender;
    private ImageView ivProfilePic;
    private TextView tvAddPhoto;
    private Button btnContinue;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerGender = findViewById(R.id.spinnerGender);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvAddPhoto = findViewById(R.id.tvAddPhoto);
        btnContinue = findViewById(R.id.btnContinue);
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvCountryCode = findViewById(R.id.tvCountryCode);

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Set up country spinner
        setupCountrySpinner();

        // Set up gender spinner
        setupGenderSpinner();

        // Set up date picker
        setupDatePicker();

        // Set up profile picture
        setupProfilePicture();

        // Set up country code selector
        tvCountryCode.setOnClickListener(v -> showCountryCodeDialog());

        // Set up continue button
        btnContinue.setOnClickListener(v -> {
            if (validateInputs()) {
                // Save user information and proceed to next screen
                saveUserInfo();
                Intent intent = new Intent(PersonalInfoActivity.this, NextActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupCountrySpinner() {
        String[] countries = new String[]{"Vietnam", "United States", "Japan", "South Korea", "China", "Singapore"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                countries
        );
        spinnerCountry.setAdapter(adapter);
        // Set default selection to Vietnam (index 0)
        spinnerCountry.setSelection(0);
    }

    private void setupGenderSpinner() {
        String[] genders = new String[]{"Chọn Giới tính", "Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genders
        );
        spinnerGender.setAdapter(adapter);
        // Set default selection to "Select Gender" (index 0)
        spinnerGender.setSelection(0);
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel();
        };

        etDateOfBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PersonalInfoActivity.this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Set max date to current date (no future dates)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void updateDateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etDateOfBirth.setText(sdf.format(calendar.getTime()));
    }

    private void setupProfilePicture() {
        View.OnClickListener photoClickListener = v -> {
            // Open image picker
            // This is a simplified version - in a real app, you would handle permissions and image picking
            Toast.makeText(PersonalInfoActivity.this, "Chọn ảnh hồ sơ", Toast.LENGTH_SHORT).show();
        };

        ivProfilePic.setOnClickListener(photoClickListener);
        tvAddPhoto.setOnClickListener(photoClickListener);
    }

    private void showCountryCodeDialog() {
        // In a real app, you would show a dialog with country codes
        // This is a simplified version
        Toast.makeText(this, "Chọn mã quốc gia", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate full name
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        }

        // Validate country (already selected by default)

        // Validate phone number (optional in this form)

        // Validate email format if provided
        String email = etEmail.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        return isValid;
    }

    private void saveUserInfo() {
        // In a real app, you would save this information to a database or shared preferences
        String fullName = etFullName.getText().toString().trim();
        String country = spinnerCountry.getSelectedItem().toString();
        String gender = spinnerGender.getSelectedItemPosition() > 0 ?
                spinnerGender.getSelectedItem().toString() : "";
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // For demonstration purposes, just show a toast
        Toast.makeText(this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }

    // Placeholder for the next activity
    public static class NextActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // This would be your next screen
        }
    }
}

