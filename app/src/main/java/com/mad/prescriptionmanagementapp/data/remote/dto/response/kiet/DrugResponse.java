package com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet;

import java.util.List;

public class DrugResponse {
    private String drugName;
    private String unitName;
    private List<DosageResponse> dosages;
    private List<ScheduleResponse> schedules;

    // Getter và Setter
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public List<DosageResponse> getDosages() { return dosages; }
    public void setDosages(List<DosageResponse> dosages) { this.dosages = dosages; }

    public List<ScheduleResponse> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleResponse> schedules) { this.schedules = schedules; }
}