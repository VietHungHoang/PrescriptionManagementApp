// User.java
package com.mad.prescriptionmanagementapp.data.model;

import java.io.Serializable;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String gender; // "male" or "female"
    private String birthDate; // Format: DD/MM/YYYY
    private String phoneNumber;
    private double weight; // kg
    private double height; // cm
    private double bmi;
    private double bmr;

    // Constructor
    public User(String username, String gender, String birthDate, String phoneNumber, double weight, double height) {
        this.username = username;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.weight = weight;
        this.height = height;
        calculateBmiAndBmr();
    }

    // Tính toán BMI và BMR
    private void calculateBmiAndBmr() {
        // BMI = weight (kg) / (height (m) * height (m))
        double heightInMeters = height / 100;
        this.bmi = weight / (heightInMeters * heightInMeters);

        // BMR (Mifflin-St Jeor)
        int age = calculateAge(birthDate);
        if (gender.equalsIgnoreCase("male")) {
            this.bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            this.bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
    }

    private int calculateAge(String birthDate) {
        try {
            String[] parts = birthDate.split("/");
            int birthYear = Integer.parseInt(parts[2]);
            int currentYear = java.time.Year.now().getValue();
            return currentYear - birthYear;
        } catch (Exception e) {
            return 22; // Giá trị mặc định nếu lỗi
        }
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }

    // Cập nhật BMI và BMR khi thay đổi thông tin
    public void updateCalculations() {
        calculateBmiAndBmr();
    }
}