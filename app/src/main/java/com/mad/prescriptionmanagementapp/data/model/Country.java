package com.mad.prescriptionmanagementapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Country {
    private Long id;
    private String name;
    private String code;

    @Override
    public String toString() {
        return this.name;
    }
}
