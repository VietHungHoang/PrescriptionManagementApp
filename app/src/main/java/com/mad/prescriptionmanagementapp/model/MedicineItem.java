package com.mad.prescriptionmanagementapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MedicineItem implements Serializable {
    private String time;
    private String date;
    private String medicineList;
    private boolean isUsed;
    private boolean isSkipped;
    private Long scheduleId;

    public MedicineItem(String time, String date, String medicineList) {
        this.time = time;
        this.date = date;
        this.medicineList = medicineList;
        this.isUsed = false;
        this.isSkipped = false;
    }
    public MedicineItem(String time, String medicineList) {
        this.time = time;
        this.medicineList = medicineList;
        this.isUsed = false;
        this.isSkipped = false;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {  // 🔹 Thêm getter cho ngày uống thuốc
        return date;
    }

    public String getMedicineList() {
        return medicineList;
    }

    public String getDetails() { // 🔹 Trả về danh sách thuốc có định dạng rõ ràng
        return medicineList.replace("\n", "\n- ");
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        this.isUsed = used;
        if (used) {
            this.isSkipped = false;
        }
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkipped(boolean skipped) {
        this.isSkipped = skipped;
        if (skipped) {
            this.isUsed = false;
        }
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
