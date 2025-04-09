package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerRegisterGoogleRequest {
    @JsonProperty("google_account_id")
    private String googleAccountId;
    private String name;
    private String email;

    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("country_id")
    private Long countryId;

    private LocalDate dob;
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String gender;
}
