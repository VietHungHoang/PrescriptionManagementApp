package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
// import androidx.room.TypeConverters; // Bỏ comment nếu dùng TypeConverter cho sectionsJson
// import com.yourpackage.data.local.db.Converters;

@Entity(tableName = "drugs")
// @TypeConverters(Converters.class) // Bỏ comment nếu dùng TypeConverter cho sectionsJson
public class DrugEntity {
    @PrimaryKey // Giả sử id từ backend là Long và là khóa chính
    public Long id;
    public String name; // drug_name
    public String title;
    public String image; // URL
    // Giả định sections được lưu dưới dạng một chuỗi JSON.
    // Nếu List<Section> phức tạp, cần tạo SectionEntity và quan hệ 1-nhiều.
    public String sectionsJson; // {"sections": [{"title": "...", "content": "..."}, ...]}

    public DrugEntity(Long id, String name, String title, String image, String sectionsJson) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.image = image;
        this.sectionsJson = sectionsJson;
    }
    public DrugEntity() {}
}