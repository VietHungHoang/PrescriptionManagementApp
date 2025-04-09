//package com.example.medicationapp;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Example button to show the options dialog
//        Button btnShowOptions = findViewById(R.id.btnShowOptions);
//        btnShowOptions.setOnClickListener(v -> showOptionsDialog());
//    }
//
//    private void showOptionsDialog() {
//        OptionsDialogFragment dialogFragment = OptionsDialogFragment.newInstance();
//        dialogFragment.show(getSupportFragmentManager(), "options_dialog");
//    }
//
//    @Override
//    public void onPrescriptionSelected() {
//        // Handle prescription option selected
//        Toast.makeText(this, "Đơn Thuốc selected", Toast.LENGTH_SHORT).show();
//        // Navigate to prescription screen
//        // Intent intent = new Intent(this, PrescriptionActivity.class);
//        // startActivity(intent);
//    }
//
//    @Override
//    public void onAppointmentSelected() {
//        // Handle appointment option selected
//        Toast.makeText(this, "Lịch Hẹn selected", Toast.LENGTH_SHORT).show();
//        // Navigate to appointment screen
//        // Intent intent = new Intent(this, AppointmentActivity.class);
//        // startActivity(intent);
//    }
//}
//
////