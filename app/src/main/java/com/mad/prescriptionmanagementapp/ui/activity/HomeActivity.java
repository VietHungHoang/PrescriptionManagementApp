package com.mad.prescriptionmanagementapp.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.data.database.AppDatabase;
import com.mad.prescriptionmanagementapp.data.database.ScheduledReminderDao;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Prescription;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduledReminderEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.databinding.ActivityHomeBinding;
import com.mad.prescriptionmanagementapp.scheduler.AlarmScheduler;
import com.mad.prescriptionmanagementapp.ui.viewmodel.HomeViewModel;
import com.mad.prescriptionmanagementapp.ui.viewmodel.PrescriptionListViewModel;
import com.mad.prescriptionmanagementapp.util.Frequency;
import com.mad.prescriptionmanagementapp.util.ReminderGenerationHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;

    private static final String TAG = "MainActivityTest";
    private AppDatabase db;
    private ScheduledReminderDao reminderDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private static final int EXACT_ALARM_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initParam();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getDatabase(getApplicationContext());
        reminderDao = db.scheduledReminderDao();


        this.binding.fab2.setOnClickListener(v -> {
            requestNotificationPermission();
            requestExactAlarmPermission(); // Gọi sau khi xin quyền notification
        });

        this.binding.fab3.setOnClickListener(v -> {
            Toast.makeText(this, "Generating test data...", Toast.LENGTH_SHORT).show();
            generateAndScheduleTestData();
        });

        this.binding.fab4.setOnClickListener(v -> {
            // Code này giúp bạn xem các reminder PENDING đã được lên lịch
            executor.execute(() -> {
                List<ScheduledReminderEntity> pending = reminderDao.getPendingReminders("PENDING", System.currentTimeMillis());
                if (pending.isEmpty()) {
                    Log.d(TAG, "No PENDING reminders in DB.");
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "No PENDING reminders in DB.", Toast.LENGTH_LONG).show());
                } else {
                    Log.d(TAG, "PENDING Reminders in DB ("+pending.size()+"):");
                    for (ScheduledReminderEntity r : pending) {
                        LocalDateTime ldt = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(r.scheduledDateTimeMillis), ZoneId.systemDefault());
                        Log.d(TAG, " - ID: " + r.id + ", Drug: " + r.drugName + ", Time: " + ldt + ", ReqCode: " + r.alarmManagerRequestId);
                    }
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Found " + pending.size() + " PENDING reminders. Check Logcat.", Toast.LENGTH_LONG).show());
                }
            });
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            } else {
                Toast.makeText(this, "Notification permission already granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Notification permission not required for this Android version.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                // Optional: intent.setData(Uri.parse("package:" + getPackageName()));
                // Tuy nhiên, không cần setData cho ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                // Hệ thống sẽ tự biết app nào đang yêu cầu.
                Toast.makeText(this, "Please grant Exact Alarm permission for reminders.", Toast.LENGTH_LONG).show();
                startActivityForResult(intent, EXACT_ALARM_PERMISSION_CODE);
            } else {
                Toast.makeText(this, "Exact Alarm permission already granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Exact Alarm permission not required for this Android version.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXACT_ALARM_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Exact Alarm permission granted by user.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Exact Alarm permission NOT granted by user.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void generateAndScheduleTestData() {
        executor.execute(() -> {
            // Xóa data cũ (nếu có) để tránh trùng lặp khi test nhiều lần
            // Hoặc bạn có thể thêm logic kiểm tra tồn tại trước khi insert
            // reminderDao.deleteAllReminders(); // Cẩn thận khi dùng hàm này

            // --- Tạo Prescription 1 ---
            Prescription prescription1 = new Prescription();
            prescription1.setId(1L); // ID đơn thuốc
            prescription1.setDoctorName("Dr. Test");
            prescription1.setConsultationDate(LocalDate.now().minusDays(1)); // Ngày khám hôm qua
            prescription1.setFollowUpDate(LocalDate.now().plusDays(30)); // Tái khám sau 30 ngày

            List<DrugInPres> drugsInPres1 = new ArrayList<>();

            // Thuốc 1: Paracetamol, uống hàng ngày, 2 lần/ngày
            DrugResponse drugResPara = new DrugResponse(101L, "Paracetamol 500mg");
            Unit unitVien = new Unit(1L, "viên");
            DrugInPres paraInPres = new DrugInPres();
            paraInPres.setDrugResponse(drugResPara);
            paraInPres.setUnit(unitVien);
            paraInPres.setDate(LocalDate.now().toString()); // Bắt đầu từ hôm nay
            paraInPres.setFrequency(Frequency.DAILY);

            List<TimeDosage> paraTimes = new ArrayList<>();
            // Nhắc sau 2 phút kể từ bây giờ để test nhanh
            LocalTime timeNow = LocalTime.now();
            paraTimes.add(new TimeDosage(timeNow.getHour(), timeNow.getMinute() + 2, 1.0));
            // Nhắc sau 4 phút kể từ bây giờ
            paraTimes.add(new TimeDosage(timeNow.getHour(), timeNow.getMinute() + 4, 1.0));
            paraInPres.setTimeDosages(paraTimes);
            drugsInPres1.add(paraInPres);


            // Thuốc 2: Vitamin C, uống cách ngày (EVERY_N_DAYS, n=2), 1 lần/ngày
            DrugResponse drugResVitC = new DrugResponse(102L, "Vitamin C 1000mg");
            Unit unitOng = new Unit(2L, "ống");
            DrugInPres vitCInPres = new DrugInPres();
            vitCInPres.setDrugResponse(drugResVitC);
            vitCInPres.setUnit(unitOng);
            vitCInPres.setDate(LocalDate.now().toString()); // Bắt đầu từ hôm nay
            vitCInPres.setFrequency(Frequency.EVERY_N_DAY);
            vitCInPres.setEveryNDays(2); // Uống cách ngày

            List<TimeDosage> vitCTimes = new ArrayList<>();
            // Nhắc sau 3 phút kể từ bây giờ (nếu hôm nay là ngày uống)
            vitCTimes.add(new TimeDosage(timeNow.getHour(), timeNow.getMinute() + 3, 1.0));
            vitCInPres.setTimeDosages(vitCTimes);
            drugsInPres1.add(vitCInPres);

            prescription1.setDrugs(drugsInPres1);

            // --- Tạo Prescription 2 (nếu muốn test nhiều đơn) ---
            // ... tương tự ...


            // Sinh ScheduledReminderEntity
            List<ScheduledReminderEntity> reminders = new ArrayList<>();
            // Sinh cho 7 ngày tới
            reminders.addAll(ReminderGenerationHelper.generateReminders(prescription1, LocalDate.now(), LocalDate.now().plusDays(7)));
            // reminders.addAll(ReminderGenerationHelper.generateReminders(prescription2, LocalDate.now(), LocalDate.now().plusDays(7)));

            if (!reminders.isEmpty()) {
                // Lưu vào DB
                reminderDao.insertAll(reminders);
                Log.d(TAG, "Inserted " + reminders.size() + " test reminders into DB.");

                // Lập lịch alarms
                AlarmScheduler.scheduleAlarmsForPendingReminders(getApplicationContext());
                Log.d(TAG, "Scheduled alarms for PENDING test reminders.");
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Test data generated and alarms scheduled!", Toast.LENGTH_LONG).show());
            } else {
                Log.d(TAG, "No reminders generated.");
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "No reminders generated (check times).", Toast.LENGTH_LONG).show());
            }
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