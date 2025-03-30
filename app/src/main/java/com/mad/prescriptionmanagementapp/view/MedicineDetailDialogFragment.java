package com.mad.prescriptionmanagementapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.model.MedicineItem;
import com.mad.prescriptionmanagementapp.model.MedicineTableItem;

import java.util.ArrayList;
import java.util.List;

public class MedicineDetailDialogFragment extends DialogFragment {
    private static final String ARG_MEDICINE = "medicine";
    private MedicineItem medicineItem;

    public static MedicineDetailDialogFragment newInstance(MedicineItem medicine) {
        MedicineDetailDialogFragment fragment = new MedicineDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE, medicine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicineItem = (MedicineItem) getArguments().getSerializable(ARG_MEDICINE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_medicine_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các thành phần giao diện
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvDate = view.findViewById(R.id.tvDate);
        RecyclerView rvMedicineList = view.findViewById(R.id.rvMedicineList);
        Button btnClose = view.findViewById(R.id.btnClose);

        // Kiểm tra View có tồn tại không
        if (tvDate == null || rvMedicineList == null) {
            throw new RuntimeException("Không tìm thấy tvDate hoặc rvMedicineList trong layout!");
        }

        // Thiết lập dữ liệu
        if (medicineItem != null) {
            tvTime.setText(medicineItem.getTime());
            tvDate.setText(medicineItem.getDate());

            // Chuyển danh sách thuốc từ chuỗi thành danh sách cặp [Tên thuốc - Số lượng]
            List<MedicineTableItem> medicineTableList = new ArrayList<>();
            String[] medicines = medicineItem.getMedicineList().split("\n");
            for (String med : medicines) {
                String[] parts = med.split(" - ");
                if (parts.length == 2) {
                    medicineTableList.add(new MedicineTableItem(parts[0], parts[1])); // [Tên thuốc, Số lượng]
                }
            }

            // Gán Adapter dạng bảng cho RecyclerView
            MedicineTableAdapter adapter = new MedicineTableAdapter(medicineTableList);
            rvMedicineList.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMedicineList.setAdapter(adapter);
        }

        // Đóng dialog khi nhấn nút
        btnClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
