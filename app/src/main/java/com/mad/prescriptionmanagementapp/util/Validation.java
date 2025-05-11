package com.mad.prescriptionmanagementapp.util;

import java.util.List;

public class Validation {
    public static <T> boolean isValidList(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
