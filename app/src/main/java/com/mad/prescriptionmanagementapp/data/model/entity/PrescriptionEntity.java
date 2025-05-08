package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.mad.prescriptionmanagementapp.data.database.Converters;

import java.time.LocalDate;

@Entity(tableName = "prescriptions")
@TypeConverters(Converters.class) // Cho LocalDate
public class PrescriptionEntity {
    @PrimaryKey // Giả sử id từ backend là Long và là khóa chính
    public Long id;
    public String hospital;
    public String doctorName;
    public LocalDate consultationDate;
    public LocalDate followUpDate;
    // List<DrugInPres> sẽ được biểu diễn qua DrugInPresEntity có prescriptionId

    public PrescriptionEntity(Long id, String hospital, String doctorName, LocalDate consultationDate, LocalDate followUpDate) {
        this.id = id;
        this.hospital = hospital;
        this.doctorName = doctorName;
        this.consultationDate = consultationDate;
        this.followUpDate = followUpDate;
    }
    public PrescriptionEntity() {}
}