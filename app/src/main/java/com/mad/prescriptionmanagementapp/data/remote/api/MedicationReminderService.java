package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.remote.dto.request.ReminderLogRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ReminderLogResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MedicationReminderService {

    @POST("/medication-logs")
    Call<ReminderLogResponse> logReminderAction( // Trả về Call<T>
                                                 @Body ReminderLogRequest logRequest
    );
}