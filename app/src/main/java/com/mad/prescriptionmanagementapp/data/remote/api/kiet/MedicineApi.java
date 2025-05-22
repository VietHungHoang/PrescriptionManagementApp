package com.mad.prescriptionmanagementapp.data.remote.api.kiet;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.MedicineResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.kiet.StatusUpdateRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet.StatusUpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MedicineApi {
    @GET("api/v1/schedule/getScheduleByDate")
    Call<MedicineResponse> getMedicinesByDate(@Query("date") String date);

    @POST("api/v1/schedule/update-status")
    Call<StatusUpdateResponse> updateStatus(@Body StatusUpdateRequest request);

    // API lấy lịch sử các ngày uống thuốc trước đây (danh sách các ngày + thuốc)
    @GET("api/v1/schedule/getHistory")
    Call<List<MedicineResponse>> getHistoryByUserId();
}
