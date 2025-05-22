package com.mad.prescriptionmanagementapp.data.model.kiet;

import java.io.Serializable;

public class MedicineItem implements Serializable {
    private String time;
    private String date;
    private Long id;
    private String medicineList;  // đổi thành String, không phải StringBuilder bên ngoài
    private boolean isUsed;
    private boolean isSkipped;
    private boolean isUsedLate;

    public boolean isUsedLate() {
        return isUsedLate;
    }

    public void setUsedLate(boolean usedLate) {
        isUsedLate = usedLate;
    }

    // constructor, getter, setter...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicineItem() {
        medicineList = new StringBuilder().toString(); // hoặc khởi tạo null và set lại sau
    }

    public MedicineItem(String time, String date, String medicineList) {
        this.time = time;
        this.date = date;
        this.medicineList = medicineList;
        this.isUsed = false;
        this.isSkipped = false;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(String medicineList) {
        this.medicineList = medicineList;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }
    // getters and setters for all fields including isUsed and isSkipped
}

