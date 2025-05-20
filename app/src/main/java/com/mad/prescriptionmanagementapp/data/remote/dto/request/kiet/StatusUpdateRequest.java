package com.mad.prescriptionmanagementapp.data.remote.dto.request.kiet;

public class StatusUpdateRequest {
    private String defaultTime;  // Thời gian uống thuốc mặc định (giờ uống thuốc)
    private int status;          // 0 = Bỏ qua, 1 = Dùng muộn, 2 = Dùng đúng giờ
    private String selectedTime; // Thời gian người dùng chọn (thời gian hiện tại)
    private boolean editted;

    public StatusUpdateRequest(String defaultTime, int status, String selectedTime, boolean editted) {
        this.defaultTime = defaultTime;
        this.status = status;
        this.selectedTime = selectedTime;
        this.editted = editted;

    }

    public boolean isEditted() {
        return editted;
    }

    public void setEditted(boolean editted) {
        this.editted = editted;
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
