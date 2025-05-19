package com.mad.prescriptionmanagementapp.api;

import com.mad.prescriptionmanagementapp.model.MedicineResponse;
import com.mad.prescriptionmanagementapp.model.StatusUpdateRequest;
import com.mad.prescriptionmanagementapp.model.StatusUpdateResponse;

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


}
