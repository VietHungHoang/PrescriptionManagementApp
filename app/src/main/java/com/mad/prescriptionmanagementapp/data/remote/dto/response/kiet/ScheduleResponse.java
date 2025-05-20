package com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet;

public class ScheduleResponse {
    private String date;
    private Object timeDosages; // JSON trả về là null, giữ Object hoặc có thể bỏ nếu không dùng

    // Getter và Setter
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Object getTimeDosages() { return timeDosages; }
    public void setTimeDosages(Object timeDosages) { this.timeDosages = timeDosages; }
}