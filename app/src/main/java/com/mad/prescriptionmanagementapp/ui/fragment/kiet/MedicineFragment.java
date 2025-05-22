package com.mad.prescriptionmanagementapp.ui.fragment.kiet;

import android.content.Intent;
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
import com.mad.prescriptionmanagementapp.adapter.kiet.MedicineAdapter;
import com.mad.prescriptionmanagementapp.api.MedicineApi;
import com.mad.prescriptionmanagementapp.data.model.kiet.MedicineItem;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.MedicineResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.kiet.StatusUpdateRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.StatusUpdateResponse;
import com.mad.prescriptionmanagementapp.ui.activity.kiet.HistoryMedicineActivity;
import com.mad.prescriptionmanagementapp.ui.listener.kiet.OnMedicineActionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicineFragment extends Fragment implements OnMedicineActionListener {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<MedicineItem> filteredMedicineList = new ArrayList<>();
    private MedicineApi medicineApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_list_kiet, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicineAdapter(filteredMedicineList, this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo Retrofit và MedicineApi chỉ khi chưa khởi tạo
        if (medicineApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")  // Đảm bảo URL là chính xác (nếu dùng Emulator)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Khởi tạo api chỉ khi chưa khởi tạo
            medicineApi = retrofit.create(MedicineApi.class);
        }

        return view;
    }


    public void updateMedicineList(String selectedDate) {
        if (medicineApi == null) {
            Log.e("MedicineApi", "MedicineApi is not initialized.");
            return;
        }
        Call<MedicineResponse> call = medicineApi.getMedicinesByDate(selectedDate);

        call.enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredMedicineList.clear();
                    String date = response.body().getDate();

                    // Tạo list MedicineItem mới, mỗi thuốc + mỗi giờ uống là một item riêng
                    for (MedicineResponse.TimeDosage dosage : response.body().getTimeDosages()) {
                        String time = dosage.getTime();

                        for (MedicineResponse.Drug drug : dosage.getDrugs()) {
                            String medicineStr = drug.getName() + " - " + drug.getDosage() + drug.getUnit();

                            MedicineItem item = new MedicineItem();
                            item.setTime(time);
                            item.setDate(date);
                            item.setMedicineList(medicineStr);
                            item.setId(dosage.getId());

                            if (dosage.isEditted()) {
                                if (dosage.getStatus() == 2) {
                                    item.setUsedLate(false);
                                    item.setUsed(true);
                                    item.setSkipped(false);
                                } else if (dosage.getStatus() == 1) {
                                    item.setUsed(false);
                                    item.setSkipped(false);
                                    item.setUsedLate(true);
                                } else if (dosage.getStatus() == 0) {
                                    item.setUsed(false);
                                    item.setSkipped(true);
                                    item.setUsedLate(false);
                                } else {
                                    item.setUsed(false);
                                    item.setSkipped(false);
                                    item.setUsedLate(false);
                                }
                            } else {
                                item.setUsed(false);
                                item.setSkipped(false);
                                item.setUsedLate(false);
                            }

                            filteredMedicineList.add(item);
                        }
                    }

                    // Sắp xếp theo giờ uống tăng dần (HH:mm)
                    filteredMedicineList.sort((item1, item2) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            Date d1 = sdf.parse(item1.getTime());
                            Date d2 = sdf.parse(item2.getTime());
                            return d1.compareTo(d2);
                        } catch (Exception e) {
                            return item1.getTime().compareTo(item2.getTime());
                        }
                    });

                    adapter.updateList(filteredMedicineList);

                    if (filteredMedicineList.isEmpty() && getContext() != null) {
                        Toast.makeText(getContext(), "Không có thuốc nào cho ngày " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                } else if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể lấy dữ liệu thuốc", Toast.LENGTH_SHORT).show();
                    Log.e("API_RESPONSE", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                }
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }



    private MedicineItem toMedicineItem(String date, MedicineResponse.TimeDosage dosage) {
        StringBuilder medicineList = new StringBuilder();
        for (MedicineResponse.Drug drug : dosage.getDrugs()) {
            medicineList.append(drug.getName())
                    .append(" - ")
                    .append(drug.getDosage())
                    .append(drug.getUnit())
                    .append("\n");
        }

        MedicineItem item = new MedicineItem(dosage.getTime(), date, medicineList.toString().trim());
         // Gán scheduleId từ backend
        return item;
    }

    @Override
    public void onUsedClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);
        Long id = item.getId();
        String date = item.getTime();
        String time = item.getTime();
        String defaultTimeString = date + "T" + time + ":00";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String selectedTimeString = dateFormat.format(calendar.getTime());

        // Tạo dialog xác nhận
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn đã dùng thuốc lúc " + time + "?")
                .setPositiveButton("OK", (dialog, which) -> {
                    sendStatusToBackend(id, 2, selectedTimeString, true);
                    item.setUsed(true);
                    Toast.makeText(getContext(), "Đã dùng thuốc lúc " + time, Toast.LENGTH_SHORT).show();

                    // Chuyển sang trang lịch sử
                    Intent intent = new Intent(getContext(), HistoryMedicineActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSkippedClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);

        String date = item.getDate();
        String time = item.getTime();
        String defaultTimeString = date + "T" + time + ":00";
        Long id = item.getId();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String selectedTimeString = dateFormat.format(calendar.getTime());

        // Tạo dialog xác nhận
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn bỏ qua thuốc lúc " + time + "?")
                .setPositiveButton("OK", (dialog, which) -> {
                    sendStatusToBackend(id, 0, selectedTimeString, true);
                    item.setSkipped(true);
                    Toast.makeText(getContext(), "Đã bỏ qua thuốc lúc " + time, Toast.LENGTH_SHORT).show();

                    // Chuyển sang trang lịch sử
                    Intent intent = new Intent(getContext(), HistoryMedicineActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }



    private void sendStatusToBackend(Long id, int status, String selectedTimeString, boolean editted) {


        // Tạo request với chuỗi thời gian
        StatusUpdateRequest request = new StatusUpdateRequest(id, status, selectedTimeString,editted);

        // Gửi yêu cầu đến backend
        medicineApi.updateStatus(request).enqueue(new Callback<StatusUpdateResponse>() {
            @Override
            public void onResponse(Call<StatusUpdateResponse> call, Response<StatusUpdateResponse> response) {
                if (response.isSuccessful()) {
                    StatusUpdateResponse statusUpdateResponse = response.body();
                    Log.d("STATUS", "Cập nhật thành công: " + statusUpdateResponse.getMessage());
                } else {
                    Log.e("STATUS", "Cập nhật thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatusUpdateResponse> call, Throwable t) {
                Log.e("STATUS", "Lỗi gửi trạng thái: " + t.getMessage(), t);
            }
        });
    }




    @Override
    public void onDetailClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);
        MedicineDetailDialogFragment dialogFragment = MedicineDetailDialogFragment.newInstance(item);
        dialogFragment.show(getChildFragmentManager(), "MedicineDetailDialog");
    }






}
