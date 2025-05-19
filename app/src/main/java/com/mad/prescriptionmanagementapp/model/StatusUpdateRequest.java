package com.mad.prescriptionmanagementapp.model;

import java.time.LocalDateTime;

public class StatusUpdateRequest {
    private String defaultTime;  // Thời gian uống thuốc mặc định (giờ uống thuốc)
    private int status;          // 0 = Bỏ qua, 1 = Dùng muộn, 2 = Dùng đúng giờ
    private String selectedTime; // Thời gian người dùng chọn (thời gian hiện tại)

    public StatusUpdateRequest(String defaultTime, int status, String selectedTime) {
        this.defaultTime = defaultTime;
        this.status = status;
        this.selectedTime = selectedTime;
    }

    public String getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(String defaultTime) {
        this.defaultTime = defaultTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }
}
