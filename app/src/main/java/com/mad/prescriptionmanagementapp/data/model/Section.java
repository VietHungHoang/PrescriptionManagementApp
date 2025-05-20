package com.mad.prescriptionmanagementapp.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    private Long id;
    @JsonProperty("section_title")
    private String title;
    private String content;
    @JsonBackReference
    private Drug drug;

}
