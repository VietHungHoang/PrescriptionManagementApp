package com.mad.prescriptionmanagementapp.data.remote.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleAuthRespone {

     @JsonProperty("google_account_id")
    private String googleAccountId;

    private String email;

    private String name;

    private String status;
    private String token;
}