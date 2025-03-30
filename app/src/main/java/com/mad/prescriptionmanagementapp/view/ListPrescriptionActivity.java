package com.mad.prescriptionmanagementapp.view;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.Prescription;

import java.util.ArrayList;
import java.util.List;

public class ListPrescriptionActivity extends AppCompatActivity {

    private MaterialButton btnRemind, btnNoRemind, btnDone;
    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private List<Prescription> prescriptionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prescription);

        // Khởi tạo các button
        btnRemind = findViewById(R.id.btn_remind);
        btnNoRemind = findViewById(R.id.btn_no_remind);
        btnDone = findViewById(R.id.btn_done);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.tabGroup);

        // Đặt mặc định tab "Đang nhắc"
        toggleGroup.check(R.id.btn_remind);
        updateTabState(btnRemind, true);
        updateTabState(btnNoRemind, false);
        updateTabState(btnDone, false);

        // Lắng nghe sự kiện chọn tab
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateTabState(btnRemind, checkedId == R.id.btn_remind);
                updateTabState(btnNoRemind, checkedId == R.id.btn_no_remind);
                updateTabState(btnDone, checkedId == R.id.btn_done);
            }
        });
        // Khởi tạo danh sách đơn thuốc mẫu
        prescriptionList = new ArrayList<>();
        prescriptionList.add(new Prescription("Đơn thuốc của bác sĩ H", "Paracetamol 500mg\nAmlodipine + Atorvastatin"));
        prescriptionList.add(new Prescription("Đơn thuốc tại bệnh viện X", "Ibuprofen 200mg\nVitamin C"));
        prescriptionList.add(new Prescription("Đơn thuốc tiểu đường", "Metformin 500mg\nInsulin"));

        prescriptionList.add(new Prescription("Đơn thuốc A", "Paracetamol 500mg\nIbuprofen 200mg\nAmoxicillin 500mg\nVitamin C\nOmega-3"));
        prescriptionList.add(new Prescription("Đơn thuốc B", "Aspirin 81mg\nMetformin 500mg"));
        prescriptionList.add(new Prescription("Đơn thuốc C", "Cefuroxime 250mg"));
        prescriptionList.add(new Prescription("Đơn thuốc D", "Losartan 50mg\nAtorvastatin 10mg\nMetformin 1000mg\nLisinopril 10mg"));
        prescriptionList.add(new Prescription("Đơn thuốc E", "Acetaminophen\nPrednisolone\nFexofenadine\nOmeprazole\nRanitidine"));

        PrescriptionAdapter adapter = new PrescriptionAdapter(this, prescriptionList);
        recyclerView.setAdapter(adapter);

        adapter = new PrescriptionAdapter(this, prescriptionList);
        recyclerView.setAdapter(adapter);
    }

    // Hiệu ứng chuyển màu tab
    private void updateTabState(MaterialButton button, boolean isSelected) {
        int fromColor = ((ColorStateList) button.getBackgroundTintList()).getDefaultColor();
        int toColor = isSelected ? getResources().getColor(R.color.tab_selected_bg_color) : getResources().getColor(R.color.tab_unselected_bg_color);
        int fromTextColor = button.getCurrentTextColor();
        int toTextColor = isSelected ? getResources().getColor(R.color.tab_selected_text_color) : getResources().getColor(R.color.tab_unselected_text_color);

        ValueAnimator colorAnimator = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimator.setDuration(300); // Thời gian hiệu ứng
        colorAnimator.addUpdateListener(animator -> button.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue())));
        colorAnimator.start();

        ValueAnimator textColorAnimator = ValueAnimator.ofArgb(fromTextColor, toTextColor);
        textColorAnimator.setDuration(300);
        textColorAnimator.addUpdateListener(animator -> button.setTextColor((int) animator.getAnimatedValue()));
        textColorAnimator.start();
    }
}
