package com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet;

import java.util.List;

public class PrescriptionResponse {
    private Long id;
    private String name;
    private String hospital;
    private String doctorName;
    private String consultationDate; // giữ String vì JSON gửi dạng "yyyy-MM-dd"
    private String followUpDate;     // có thể null
    private Integer status;
    private List<DrugResponse> drugs;

    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getConsultationDate() { return consultationDate; }
    public void setConsultationDate(String consultationDate) { this.consultationDate = consultationDate; }

    public String getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(String followUpDate) { this.followUpDate = followUpDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public List<DrugResponse> getDrugs() { return drugs; }
    public void setDrugs(List<DrugResponse> drugs) { this.drugs = drugs; }
}






