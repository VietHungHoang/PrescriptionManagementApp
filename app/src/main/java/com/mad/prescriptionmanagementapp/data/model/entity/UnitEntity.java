package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitEntity {
    @PrimaryKey
    public Long id;
    public String name;
}
