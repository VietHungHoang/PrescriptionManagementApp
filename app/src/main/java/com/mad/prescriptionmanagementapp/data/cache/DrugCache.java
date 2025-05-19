package com.mad.prescriptionmanagementapp.data.cache;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "drug_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugCache {
    @PrimaryKey
    private Long id;

    private String name;
}
