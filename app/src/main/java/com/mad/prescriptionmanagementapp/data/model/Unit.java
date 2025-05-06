package com.mad.prescriptionmanagementapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    @PrimaryKey
    private Long id;
    private String name;
}
