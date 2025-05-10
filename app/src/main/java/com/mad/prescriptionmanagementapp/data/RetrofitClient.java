package com.mad.prescriptionmanagementapp.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.data.remote.api.DrugService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static DrugService getDrugService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.106:8080/api/v1/") // Dùng 10.0.2.2 cho emulator
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .build();
        }
        return retrofit.create(DrugService.class);
    }
}