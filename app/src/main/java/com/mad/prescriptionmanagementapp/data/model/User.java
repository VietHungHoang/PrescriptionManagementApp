package com.mad.prescriptionmanagementapp.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String email;
    private String name;
    private String dateOfBirth;
    private String facebookAccountId;
    private String googleAccountId;
    private String password;
    private String phoneNumber;
    private String photoUrl;
    private Long countryId;
    private Long roleId;
    private String gender;

    public User() {}

    public User(String name, String dateOfBirth, String phoneNumber, String gender, Double weight, Double height) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; } // Thay getUsername() bằng getName()
    public void setName(String name) { this.name = name; }
    public String getDateOfBirth() { return dateOfBirth; } // Thay getBirthDate() bằng getDateOfBirth()
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getFacebookAccountId() { return facebookAccountId; }
    public void setFacebookAccountId(String facebookAccountId) { this.facebookAccountId = facebookAccountId; }
    public String getGoogleAccountId() { return googleAccountId; }
    public void setGoogleAccountId(String googleAccountId) { this.googleAccountId = googleAccountId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // Xóa getWeight(), getHeight(), getBmi(), getBmr() vì chúng được lấy từ UserSetting
    public void updateCalculations() {
        // Nếu cần thêm logic
    }
}
