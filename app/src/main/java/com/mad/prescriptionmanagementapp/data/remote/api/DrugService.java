package com.mad.prescriptionmanagementapp.data.remote.api;

import com.mad.prescriptionmanagementapp.data.model.Drug;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.ResponseObject;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DrugService {

    @GET("drugs/simple")
    Call<ResponseObject<List<DrugResponse>>> getDrugsSimple();

    @GET("drugs/unit")
    Call<ResponseObject<List<UnitResponse>>> getAllUnit();

    @GET("drugs")
    Call<ResponseObject<List<Drug>>> getAllDrugs();

    @GET("drugs/search")
    Call<ResponseObject<List<Drug>>> searchDrugs(@Query("query") String query);

    @GET("drugs/{id}")
    Call<ResponseObject<Drug>> getDrugById(@Path("id") Long id);
}