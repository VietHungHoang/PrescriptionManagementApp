package com.mad.prescriptionmanagementapp.model;

public class StatusUpdateResponse {

    private int status;  // Trạng thái sau khi xử lý
    private String message;  // Thông báo

    public StatusUpdateResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getter và Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
