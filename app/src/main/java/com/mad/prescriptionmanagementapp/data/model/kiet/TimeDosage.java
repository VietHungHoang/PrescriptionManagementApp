package com.mad.prescriptionmanagementapp.data.model.kiet;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TimeDosage {
    private int hour;
    private int minutes;
    private double dosage;
    private Long id;
    public static TimeDosage deepCopy(TimeDosage other) {
        if (other == null) {
            return null;
        }
        return new TimeDosage(other.hour, other.minutes, other.dosage, other.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static List<TimeDosage> deepCopyList(List<TimeDosage> originalList) {
        List<TimeDosage> deepCopiedList = new ArrayList<>();
        if (originalList != null) {
            for (TimeDosage td : originalList) {
                TimeDosage copiedTimeDosage = TimeDosage.deepCopy(td);
                deepCopiedList.add(copiedTimeDosage);
            }
        }
        return deepCopiedList;
    }

    public void copy(TimeDosage other) {
        this.id = other.getId();
        this.dosage = other.getDosage();
        this.hour = other.getHour();
        this.minutes = other.getMinutes();
    }
}