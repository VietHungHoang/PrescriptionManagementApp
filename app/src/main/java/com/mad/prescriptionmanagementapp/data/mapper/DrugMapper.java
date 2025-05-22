package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;

import org.modelmapper.ModelMapper;

public class DrugMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static DrugEntity responseToCache(SimpleDrug drug) {
        return modelMapper.map(drug, DrugEntity.class);
    }
    public static SimpleDrug cacheToResponse(DrugEntity drug) {
        return modelMapper.map(drug, SimpleDrug.class);
    }
}
