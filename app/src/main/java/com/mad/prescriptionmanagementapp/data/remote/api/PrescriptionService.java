package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PrescriptionService {

    @POST("prescriptions")
    Call<ResponseObject<Void>> saveToServer(@Body PrescriptionRequest prescriptionRequest);
}