package com.mad.prescriptionmanagementapp.data.remote;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.data.remote.api.LoginService;
import com.mad.prescriptionmanagementapp.data.remote.api.UserService;
import com.mad.prescriptionmanagementapp.util.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NetworkClient {

    private static Retrofit retrofit = null;
    private static LoginService loginService = null;
    private static UserService userService = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Avoid error if json have a field cannot map

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .build();
        }
        return retrofit;
    }

    public static LoginService getAuthService() {
        if (loginService == null) {
            loginService = getClient().create(LoginService.class);
        }
        return loginService;
    }

    public static UserService getUserService() {
        if (NetworkClient.userService == null) {
            NetworkClient.userService = NetworkClient.getClient().create(UserService.class);
        }
        return NetworkClient.userService;
    }
}