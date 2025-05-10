package com.mad.prescriptionmanagementapp.ui.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.data.model.Section;
import com.mad.prescriptionmanagementapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class DrugDetailActivity extends Activity {

    private static final String TAG = "DrugDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        try {
            // Lấy dữ liệu từ Intent
            String jsonData = getIntent().getStringExtra("drug_data");
            ObjectMapper mapper = new ObjectMapper();
            Drug drug = mapper.readValue(jsonData, Drug.class);

            // Hiển thị thông tin
            TextView drugNameText = findViewById(R.id.drug_name);
            TextView drugTitleText = findViewById(R.id.drug_title);
            ImageView drugImageView = findViewById(R.id.drug_image);
            RecyclerView sectionsRecyclerView = findViewById(R.id.sections_recycler_view);
            ImageButton backButton = findViewById(R.id.back_button);
            NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);

            // Kiểm tra null cho các view
            if (drugNameText == null || drugTitleText == null || drugImageView == null || sectionsRecyclerView == null || backButton == null || nestedScrollView == null) {
                Log.e(TAG, "One or more views not found in layout. Check activity_medicine_detail.xml");
                return;
            }

            drugNameText.setText(drug.getName() != null ? drug.getName() : "Không có tên");
            drugTitleText.setText(drug.getTitle() != null ? drug.getTitle() : "Không có tiêu đề");

            // Tải ảnh bằng Picasso
            if (drug.getImage() != null && !drug.getImage().isEmpty()) {
                Picasso.get()
                        .load(drug.getImage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(drugImageView);
            } else {
                drugImageView.setImageResource(R.drawable.placeholder_image);
            }

            // Thiết lập RecyclerView cho sections
            List<Section> sections = drug.getSections() != null ? drug.getSections() : new ArrayList<>();
            SectionAdapter sectionAdapter = new SectionAdapter(sections);
            sectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            sectionsRecyclerView.setHasFixedSize(true);
            sectionsRecyclerView.setAdapter(sectionAdapter);

            // Yêu cầu cập nhật layout của NestedScrollView
            nestedScrollView.requestLayout();

            // Xử lý sự kiện nhấn nút back
            backButton.setOnClickListener(v -> {
                Log.d(TAG, "Back button pressed");
                finish();
            });

            Log.d(TAG, "Sections count: " + sections.size());
            Log.d(TAG, "Displayed drug details: " + (drug.getName() != null ? drug.getName() : "unknown"));
        } catch (Exception e) {
            Log.e(TAG, "Error displaying drug details", e);
        }
    }
}