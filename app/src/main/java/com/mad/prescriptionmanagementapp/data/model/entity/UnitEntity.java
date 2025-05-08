package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

    @Entity(tableName = "units")
    public class UnitEntity {
        @PrimaryKey // Giả sử id từ backend là Long và là khóa chính
        public Long id;
        public String name;

        public UnitEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        public UnitEntity() {} // Room cần constructor không tham số
    }
