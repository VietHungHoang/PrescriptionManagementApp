package com.mad.prescriptionmanagementapp.data.remote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseObject<T> {
    private String message;
    private String status;
    private T data;
}

