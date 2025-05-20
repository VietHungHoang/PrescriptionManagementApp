package com.mad.prescriptionmanagementapp.data.model.kiet;

public class DayModel {
    private String dayOfWeek; // VD: "T2"
    private int dayNumber;    // VD: 13
    private int month;        // VD: 4 (tháng 4)

    public DayModel(String dayOfWeek, int dayNumber, int month) {
        this.dayOfWeek = dayOfWeek;
        this.dayNumber = dayNumber;
        this.month = month;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public int getDayNumber() { return dayNumber; }
    public int getMonth() { return month; }
}
