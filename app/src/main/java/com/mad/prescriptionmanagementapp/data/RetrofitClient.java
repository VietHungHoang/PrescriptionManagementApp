package com.mad.prescriptionmanagementapp.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.data.remote.api.DrugService;
import com.mad.prescriptionmanagementapp.data.remote.api.ApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Bỏ qua các trường không nhận diện
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.106:8080/api/v1/") // Sử dụng 10.0.2.2 cho emulator, thay đổi nếu cần
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .build();
        }
        return retrofit;
    }

    public static DrugService getDrugService() {
        return getRetrofitInstance().create(DrugService.class);
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}