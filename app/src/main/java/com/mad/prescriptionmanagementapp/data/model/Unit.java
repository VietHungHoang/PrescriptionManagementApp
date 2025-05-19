package com.mad.prescriptionmanagementapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    private Long id;
    private String name;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
