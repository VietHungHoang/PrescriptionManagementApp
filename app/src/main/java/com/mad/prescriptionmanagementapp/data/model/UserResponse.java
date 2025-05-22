package com.mad.prescriptionmanagementapp.data.model;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private Long id;
    private String email;
    private String name;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // Chuyển đổi sang User
    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setDateOfBirth(dateOfBirth);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);
        return user;
    }
}