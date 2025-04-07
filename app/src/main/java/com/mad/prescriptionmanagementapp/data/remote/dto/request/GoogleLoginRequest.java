package com.mad.prescriptionmanagementapp.data.remote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleLoginRequest {
    private String idToken;
}

