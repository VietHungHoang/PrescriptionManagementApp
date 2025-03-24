package com.mad.prescriptionmanagementapp;

public class DayItem {
    public String weekday;  // Ví dụ: "T2", "T3", ..., "CN"
    public String date;     // Ví dụ: "20", "21", ..., "26"

    public DayItem(String weekday, String date) {
        this.weekday = weekday;
        this.date = date;
    }
}
