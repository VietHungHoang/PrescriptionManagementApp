package com.mad.prescriptionmanagementapp.ui.fragment.kiet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.kiet.HistoryDayAdapter;
import com.mad.prescriptionmanagementapp.data.remote.api.kiet.MedicineApi;
import com.mad.prescriptionmanagementapp.data.model.kiet.MedicineItem;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.MedicineResponse;
import com.mad.prescriptionmanagementapp.data.model.kiet.RetrofitClient;
import com.mad.prescriptionmanagementapp.ui.listener.kiet.OnMedicineActionListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements OnMedicineActionListener {

    private RecyclerView rvDays;
    private HistoryDayAdapter dayAdapter;
    private List<MedicineResponse> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_item_kiet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvDays = view.findViewById(R.id.recyclerView);
        rvDays.setLayoutManager(new LinearLayoutManager(requireContext()));

        dayAdapter = new HistoryDayAdapter(historyList, this);
        rvDays.setAdapter(dayAdapter);

        loadHistoryData();
    }

    private void loadHistoryData() {
        MedicineApi medicineApi = RetrofitClient.getInstance().create(MedicineApi.class);
        medicineApi.getHistoryByUserId().enqueue(new Callback<List<MedicineResponse>>() {
            @Override
            public void onResponse(Call<List<MedicineResponse>> call, Response<List<MedicineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    dayAdapter.updateData(historyList);  // Dùng method cập nhật và notify
                } else {
                    Toast.makeText(getContext(), "Không lấy được dữ liệu lịch sử", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MedicineResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onUsedClicked(int position) { }

    @Override
    public void onSkippedClicked(int position) { }

    @Override
    public void onDetailClicked(int position) {
        MedicineItem item = dayAdapter.getMedicineItemByPosition(position);
        if (item != null) {
            MedicineDetailDialogFragment dialogFragment = MedicineDetailDialogFragment.newInstance(item);
            dialogFragment.show(getChildFragmentManager(), "MedicineDetailDialog");
        }
    }
}


