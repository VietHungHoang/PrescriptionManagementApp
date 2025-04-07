package com.mad.prescriptionmanagementapp.data.model;

public class Customer extends User{
    public Customer(String googleAccountId, String email, String name, String photoUrl) {
        super(googleAccountId, email, name, photoUrl);
    }
}
