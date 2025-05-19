package com.mad.prescriptionmanagementapp.view;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.adapter.MedicineAdapter;
import com.mad.prescriptionmanagementapp.api.MedicineApi;
import com.mad.prescriptionmanagementapp.model.MedicineItem;
import com.mad.prescriptionmanagementapp.model.MedicineResponse;
import com.mad.prescriptionmanagementapp.model.StatusUpdateRequest;
import com.mad.prescriptionmanagementapp.model.StatusUpdateResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        View view = inflater.inflate(R.layout.fragment_medicine_list, container, false);

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
        // Kiểm tra xem medicineApi có null không trước khi gọi API
        if (medicineApi == null) {
            Log.e("MedicineApi", "MedicineApi is not initialized.");
            // Kiểm tra context có null không trước khi gọi Toast
            if (getContext() != null) {
                Toast.makeText(getContext(), "Không thể kết nối với API", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Call<MedicineResponse> call = medicineApi.getMedicinesByDate(selectedDate);

        call.enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredMedicineList.clear();
                    String date = response.body().getDate();

                    // Tạo một Map để nhóm thuốc theo giờ
                    Map<String, StringBuilder> timeGroupedMedicines = new HashMap<>();

                    for (MedicineResponse.TimeDosage dosage : response.body().getTimeDosages()) {
                        String time = dosage.getTime();  // Lấy giờ từ liều thuốc
                        StringBuilder medicineList = timeGroupedMedicines.getOrDefault(time, new StringBuilder());

                        // Thêm thuốc vào list của giờ tương ứng
                        for (MedicineResponse.Drug drug : dosage.getDrugs()) {
                            medicineList.append(drug.getName())
                                    .append(" - ")
                                    .append(drug.getDosage())
                                    .append(drug.getUnit())
                                    .append("\n");
                        }

                        // Cập nhật lại vào map
                        timeGroupedMedicines.put(time, medicineList);
                    }

                    // Chuyển Map thành danh sách các MedicineItem
                    for (Map.Entry<String, StringBuilder> entry : timeGroupedMedicines.entrySet()) {
                        String time = entry.getKey();
                        StringBuilder medicineList = entry.getValue();

                        MedicineItem item = new MedicineItem(time, date, medicineList.toString().trim());
                        filteredMedicineList.add(item);
                    }

                    // Cập nhật danh sách cho adapter
                    adapter.updateList(filteredMedicineList);

                    if (filteredMedicineList.isEmpty()) {
                        // Kiểm tra context có null không trước khi gọi Toast
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Không có thuốc nào cho ngày " + selectedDate, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Kiểm tra context có null không trước khi gọi Toast
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Không thể lấy dữ liệu thuốc", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("API_RESPONSE", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                // Kiểm tra context có null không trước khi gọi Toast
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
        item.setScheduleId(dosage.getId()); // Gán scheduleId từ backend
        return item;
    }

    @Override
    public void onUsedClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);

        // Kết hợp date và time để tạo thành chuỗi thời gian ISO 8601
        String date = item.getDate();  // Ví dụ: "2025-05-12"
        String time = item.getTime();  // Ví dụ: "12:00"

        // Tạo chuỗi thời gian mặc định theo định dạng "yyyy-MM-dd'T'HH:mm:ss"
        String defaultTimeString = date + "T" + time + ":00";  // "2025-05-12T12:00:00"

        // Lấy thời gian hiện tại mà người dùng chọn
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String selectedTimeString = dateFormat.format(calendar.getTime());  // Thời gian hiện tại (ISO 8601)

        // Gửi yêu cầu "Dùng" (status = 2)
        sendStatusToBackend(defaultTimeString, 2, selectedTimeString);  // Gửi chuỗi thay vì LocalDateTime

        // Đánh dấu thuốc là đã dùng và cập nhật giao diện
        item.setUsed(true);
        Toast.makeText(getContext(), "Đã dùng thuốc lúc " + item.getTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSkippedClicked(int position) {
        MedicineItem item = filteredMedicineList.get(position);

        // Kết hợp date và time để tạo thành chuỗi thời gian ISO 8601
        String date = item.getDate();  // Ví dụ: "2025-05-12"
        String time = item.getTime();  // Ví dụ: "12:00"

        // Tạo chuỗi thời gian mặc định theo định dạng "yyyy-MM-dd'T'HH:mm:ss"
        String defaultTimeString = date + "T" + time + ":00";  // "2025-05-12T12:00:00"

        // Lấy thời gian hiện tại mà người dùng chọn
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String selectedTimeString = dateFormat.format(calendar.getTime());  // Thời gian hiện tại (ISO 8601)

        // Gửi yêu cầu "Bỏ qua" (status = 0)
        sendStatusToBackend(defaultTimeString, 0, selectedTimeString);  // Gửi chuỗi thay vì LocalDateTime

        // Đánh dấu thuốc là đã bỏ qua và cập nhật giao diện
        item.setSkipped(true);
        Toast.makeText(getContext(), "Đã bỏ qua thuốc lúc " + item.getTime(), Toast.LENGTH_SHORT).show();
    }

    private void sendStatusToBackend(String defaultTimeString, int status, String selectedTimeString) {
        if (defaultTimeString == null || selectedTimeString == null) {
            Log.e("STATUS", "Thời gian không hợp lệ");
            return;
        }

        // Tạo request với chuỗi thời gian
        StatusUpdateRequest request = new StatusUpdateRequest(defaultTimeString, status, selectedTimeString);

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
