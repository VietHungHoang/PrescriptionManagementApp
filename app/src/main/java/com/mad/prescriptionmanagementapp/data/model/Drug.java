package com.mad.prescriptionmanagementapp.data.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drug {
    private Long id;

    @JsonProperty("drug_name")
    private String name;

    private String title;
    private String image;
    @JsonManagedReference
    private List<Section> sections = new ArrayList<>();
}