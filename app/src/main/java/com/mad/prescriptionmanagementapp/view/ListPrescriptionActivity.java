package com.mad.prescriptionmanagementapp.view;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.MixedPrescriptionAdapter;
import com.mad.prescriptionmanagementapp.model.BaseItem;
import com.mad.prescriptionmanagementapp.model.PrescriptionGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ListPrescriptionActivity extends AppCompatActivity {

    private MaterialButton btnRemind, btnDone;
    private RecyclerView recyclerView;
    private PrescriptionApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prescription);

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Địa chỉ localhost khi chạy trên emulator Android
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(PrescriptionApi.class);

        // Khởi tạo các button và recyclerView
        btnRemind = findViewById(R.id.btn_remind);
        btnDone = findViewById(R.id.btn_done);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.tabGroup);

        // Đặt mặc định tab "Đang nhắc"
        toggleGroup.check(R.id.btn_remind);
        updateTabState(btnRemind, true);
        updateTabState(btnDone, false);

        // Lắng nghe sự kiện chọn tab
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateTabState(btnRemind, checkedId == R.id.btn_remind);
                updateTabState(btnDone, checkedId == R.id.btn_done);

                int status = -1;
                if (checkedId == R.id.btn_remind) status = 0; // Giả sử status 0 là Đang nhắc
                else if (checkedId == R.id.btn_done) status = 1; // Giả sử status 1 là Đã xong

                if (status != -1) {
                    loadPrescriptions(status);
                }
            }
        });

        // Gọi load mặc định cho tab "Đang nhắc"
        loadPrescriptions(0);
    }

    // Gọi API lấy danh sách đơn thuốc theo status
    private void loadPrescriptions(int status) {
        api.getPrescriptionsByStatus(status).enqueue(new Callback<List<PrescriptionGroup>>() {
            @Override
            public void onResponse(Call<List<PrescriptionGroup>> call, Response<List<PrescriptionGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BaseItem> items = new ArrayList<>();
                    items.addAll(response.body()); // Chuyển list nhận về thành list BaseItem

                    MixedPrescriptionAdapter adapter = new MixedPrescriptionAdapter(items);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Xử lý lỗi hoặc hiển thị thông báo
                }
            }

            @Override
            public void onFailure(Call<List<PrescriptionGroup>> call, Throwable t) {
                // Xử lý lỗi mạng
                t.printStackTrace();
            }
        });
    }

    // Hiệu ứng chuyển màu tab
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

    // Retrofit interface cho API
    public interface PrescriptionApi {
        @GET("api/v1/prescription/{status}")
        Call<List<PrescriptionGroup>> getPrescriptionsByStatus(@Path("status") int status);
    }
}
