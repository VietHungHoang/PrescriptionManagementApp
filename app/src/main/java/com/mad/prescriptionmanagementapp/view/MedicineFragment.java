package com.mad.prescriptionmanagementapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.prescriptionmanagementapp.model.MedicineItem;
import com.mad.prescriptionmanagementapp.R;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedicineFragment extends Fragment implements OnMedicineActionListener {
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<MedicineItem> allMedicineList;  // Danh sách gốc
    private List<MedicineItem> filteredMedicineList; // Danh sách sau khi lọc

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Giả lập dữ liệu thuốc
        allMedicineList = new ArrayList<>();
        allMedicineList.add(new MedicineItem("09:15", "30/03/2025", "Paracetamol 500mg - 1 viên\nAmlodipine + Atorvastatin - 2 viên"));
        allMedicineList.add(new MedicineItem("14:30", "30/03/2025", "Ibuprofen 400mg - 1 viên\nCefuroxime 500mg - 1 viên"));
        allMedicineList.add(new MedicineItem("08:00", "31/03/2025", "Vitamin C 1000mg - 1 viên\nMetformin 500mg - 1 viên"));
        allMedicineList.add(new MedicineItem("12:00", "31/03/2025", "Omeprazole 20mg - 1 viên\nLisinopril 5mg - 1 viên"));

        // Mặc định hiển thị tất cả thuốc
        filteredMedicineList = new ArrayList<>(allMedicineList);
        adapter = new MedicineAdapter(filteredMedicineList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Hàm cập nhật danh sách thuốc dựa trên ngày được chọn từ CalendarFragment
    public void updateMedicineList(String selectedDate) {
        if (selectedDate == null || selectedDate.isEmpty()) return;

        // Lọc danh sách thuốc theo ngày được chọn
        filteredMedicineList = allMedicineList.stream()
                .filter(medicine -> medicine.getDate().equals(selectedDate))
                .collect(Collectors.toList());

        // Cập nhật RecyclerView
        adapter.updateList(filteredMedicineList);

        // Thông báo nếu không có thuốc nào cho ngày đã chọn
        if (filteredMedicineList.isEmpty()) {
            Toast.makeText(getContext(), "Không có thuốc nào cho ngày " + selectedDate, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUsedClicked(int position) {
        Toast.makeText(getContext(), "Bạn đã dùng thuốc vào " + filteredMedicineList.get(position).getTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSkippedClicked(int position) {
        Toast.makeText(getContext(), "Bạn đã bỏ qua thuốc vào " + filteredMedicineList.get(position).getTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetailClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);

        // Hiển thị MedicineDetailDialogFragment
        MedicineDetailDialogFragment dialogFragment = MedicineDetailDialogFragment.newInstance(item);
        dialogFragment.show(getChildFragmentManager(), "MedicineDetailDialog");
    }
}
