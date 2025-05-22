package com.mad.prescriptionmanagementapp.data.model;

import java.io.Serializable;

public class UserSetting implements Serializable {
    private Long id;
    private Long userId;
    private String name;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;
    private Double weight;
    private Double height;

    public UserSetting() {}

    public UserSetting(Long userId, String name, String dateOfBirth, String phoneNumber, String gender, Double weight, Double height) {
        this.userId = userId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
}