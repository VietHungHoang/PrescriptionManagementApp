package com.mad.prescriptionmanagementapp.ui.activity.kiet;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.kiet.PrescriptionGroupAdapter;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.PrescriptionResponse;
import com.mad.prescriptionmanagementapp.util.SharedPrefUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;

public class ListPrescriptionActivity extends AppCompatActivity {

    private MaterialButton btnRemind, btnDone;
    private RecyclerView recyclerView;
    private PrescriptionGroupAdapter adapter;
    private PrescriptionApi api;
    private ImageView btnBack;

    private SharedPrefUtils sharedPrefUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prescription_kiet);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
//        bottomNav.setSelectedItemId(R.id.nav_invoice);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.100:8080/") // Địa chỉ localhost trên emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // hoặc finish();
        });

        api = retrofit.create(PrescriptionApi.class);

        btnRemind = findViewById(R.id.btn_remind);
        btnDone = findViewById(R.id.btn_done);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPrefUtils = new SharedPrefUtils(this);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.tabGroup);
        loadPrescriptions(0);
        toggleGroup.check(R.id.btn_remind);
        updateTabState(btnRemind, true);
        updateTabState(btnDone, false);


                toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateTabState(btnRemind, checkedId == R.id.btn_remind);
                updateTabState(btnDone, checkedId == R.id.btn_done);

                int status = -1;
                if (checkedId == R.id.btn_remind) status = 0;
                else if (checkedId == R.id.btn_done) status = 1;

                if (status != -1) {
                    loadPrescriptions(status);
                }
            }
        });



        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calendar) {
                startActivity(new Intent(ListPrescriptionActivity.this, MedicineScheduleActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadPrescriptions(int status) {
        api.getPrescriptionsByStatus("Bearer " + sharedPrefUtils.getToken(), status).enqueue(new Callback<List<PrescriptionResponse>>() {
            @Override
            public void onResponse(Call<List<PrescriptionResponse>> call, Response<List<PrescriptionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PrescriptionResponse> prescriptionList = response.body();

                    adapter = new PrescriptionGroupAdapter(prescriptionList, prescription -> {
                        // Xử lý sự kiện khi nhấn nút chỉnh sửa nhóm
                        Toast.makeText(ListPrescriptionActivity.this, "Chỉnh sửa đơn: " + prescription.getName(), Toast.LENGTH_SHORT).show();
                    });
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(ListPrescriptionActivity.this, "Không có dữ liệu đơn thuốc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PrescriptionResponse>> call, Throwable t) {
                Toast.makeText(ListPrescriptionActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTabState(MaterialButton button, boolean isSelected) {
        int fromColor = ((ColorStateList) button.getBackgroundTintList()).getDefaultColor();
        int toColor = isSelected ? getResources().getColor(R.color.tab_selected_bg_color) : getResources().getColor(R.color.tab_unselected_bg_color);
        int fromTextColor = button.getCurrentTextColor();
        int toTextColor = isSelected ? getResources().getColor(R.color.tab_selected_text_color) : getResources().getColor(R.color.tab_unselected_text_color);

        ValueAnimator colorAnimator = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimator.setDuration(300);
        colorAnimator.addUpdateListener(animator -> button.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue())));
        colorAnimator.start();

        ValueAnimator textColorAnimator = ValueAnimator.ofArgb(fromTextColor, toTextColor);
        textColorAnimator.setDuration(300);
        textColorAnimator.addUpdateListener(animator -> button.setTextColor((int) animator.getAnimatedValue()));
        textColorAnimator.start();
    }

    public interface PrescriptionApi {
        @retrofit2.http.GET("api/v1/prescriptions/getByStatus")
        retrofit2.Call<List<PrescriptionResponse>> getPrescriptionsByStatus(@Header("Authorization") String authHeader, @retrofit2.http.Query("status") int status);
    }
}
