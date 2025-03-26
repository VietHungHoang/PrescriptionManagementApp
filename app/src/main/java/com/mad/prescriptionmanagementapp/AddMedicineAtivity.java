//package com.mad.prescriptionmanagementapp;
//package com.example.medicationapp;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class AddMedicationActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_medication);
//
//        // Initialize views
//        ImageButton btnClose = findViewById(R.id.btnClose);
//        AutoCompleteTextView actvUnit = findViewById(R.id.actvUnit);
//        Button btnSave = findViewById(R.id.btnSave);
//
//        // Set up close button
//        btnClose.setOnClickListener(v -> finish());
//
//        // Set up unit dropdown
//        String[] units = new String[]{"Viên", "Gói", "Ống", "Ml", "Mg"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                units
//        );
//        actvUnit.setAdapter(adapter);
//
//        // Set up save button
//        btnSave.setOnClickListener(v -> {
//            // Validate and save medication
//            if (validateInputs()) {
//                saveMedication();
//                finish();
//            }
//        });
//    }
//
//    private boolean validateInputs() {
//        // Get input values
//        String medicineName = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etMedicineName)).getText().toString().trim();
//        String unit = ((AutoCompleteTextView) findViewById(R.id.actvUnit)).getText().toString().trim();
//
//        // Validate required fields
//        if (medicineName.isEmpty()) {
//            Toast.makeText(this, "Vui lòng nhập tên thuốc", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (unit.isEmpty()) {
//            Toast.makeText(this, "Vui lòng chọn đơn vị", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }
//
//    private void saveMedication() {
//        // Get all input values
//        String medicineName = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etMedicineName)).getText().toString().trim();
//        String unit = ((AutoCompleteTextView) findViewById(R.id.actvUnit)).getText().toString().trim();
//        boolean isByDose = ((android.widget.RadioButton) findViewById(R.id.rbByDose)).isChecked();
//        String notes = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.etNotes)).getText().toString().trim();
//
//        // Here you would typically save to a database
//        // For demonstration, just show a success message
//        Toast.makeText(this, "Đã lưu thuốc thành công", Toast.LENGTH_SHORT).show();
//    }
//}
//
