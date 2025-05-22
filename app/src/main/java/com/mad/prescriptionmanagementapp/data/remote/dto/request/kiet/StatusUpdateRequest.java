package com.mad.prescriptionmanagementapp.data.remote.dto.request.kiet;

public class StatusUpdateRequest {
    private Long  id;  // Thời gian uống thuốc mặc định (giờ uống thuốc)
    private int status;          // 0 = Bỏ qua, 1 = Dùng muộn, 2 = Dùng đúng giờ
    private String selectedTime; // Thời gian người dùng chọn (thời gian hiện tại)
    private boolean editted;

    public StatusUpdateRequest(Long id, int status, String selectedTime, boolean editted) {
        this.id = id;
        this.status = status;
        this.selectedTime = selectedTime;
        this.editted = editted;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEditted() {
        return editted;
    }

    public void setEditted(boolean editted) {
        this.editted = editted;
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
