package com.mad.prescriptionmanagementapp.model;

import java.util.List;

public class MedicineResponse {
    private String date;
    private List<TimeDosage> timeDosages;

    public String getDate() {
        return date;
    }

    public List<TimeDosage> getTimeDosages() {
        return timeDosages;
    }

    public static class TimeDosage {
        private Long id;            // ✅ ID của schedule
        private String time;
        private List<Drug> drugs;

        public Long getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public List<Drug> getDrugs() {
            return drugs;
        }
    }

    public static class Drug {
        private String name;
        private double dosage;
        private String unit;

        public String getName() {
            return name;
        }

        public double getDosage() {
            return dosage;
        }

        public String getUnit() {
            return unit;
        }
    }
}
