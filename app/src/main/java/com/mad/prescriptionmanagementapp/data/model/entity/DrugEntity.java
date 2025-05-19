package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "drugs")
// @TypeConverters(Converters.class) // Bỏ comment nếu dùng TypeConverter cho sectionsJson
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugEntity {
    @PrimaryKey
    public Long id;
    public String name;
}