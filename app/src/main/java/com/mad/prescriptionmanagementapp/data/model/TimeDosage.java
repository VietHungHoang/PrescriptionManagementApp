package com.mad.prescriptionmanagementapp.data.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeDosage {
    private int hour;
    private int minutes;
    private double dosage;

    public static TimeDosage deepCopy(TimeDosage other) {
        if (other == null) {
            return null;
        }
        return new TimeDosage(other.hour, other.minutes, other.dosage);
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
}
