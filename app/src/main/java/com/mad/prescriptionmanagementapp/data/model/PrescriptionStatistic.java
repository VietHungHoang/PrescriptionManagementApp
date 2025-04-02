package com.mad.prescriptionmanagementapp.data.model;

import java.time.LocalDate;

public class PrescriptionStatistic {
    private int id;
    private String medicineName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int cntLate;
    private int cntEarly;
    private int cntNotDrink;

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getCntLate() {
        return cntLate;
    }

    public void setCntLate(int cntLate) {
        this.cntLate = cntLate;
    }

    public int getCntNotDrink() {
        return cntNotDrink;
    }

    public void setCntNotDrink(int cntNotDrink) {
        this.cntNotDrink = cntNotDrink;
    }

    public int getCntEarly() {
        return cntEarly;
    }

    public void setCntEarly(int cntEarly) {
        this.cntEarly = cntEarly;
    }
}
