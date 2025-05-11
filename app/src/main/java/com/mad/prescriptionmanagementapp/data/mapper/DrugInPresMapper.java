package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class DrugInPresMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static DrugInPresEntity modelToEntity(DrugInPres drugInPres) {
        PropertyMap<DrugInPres, DrugInPresEntity> prescriptionMap = new PropertyMap<DrugInPres, DrugInPresEntity>() {
            @Override
            protected void configure() {
                map(source.getDrug().getId(), destination.getDrugId());  // Đổi tên trường
                map(source.getUnit().getId(), destination.getUnitId());  // Đổi tên trường
            }
        };
        modelMapper.addMappings(prescriptionMap);
        return modelMapper.map(drugInPres, DrugInPresEntity.class);
    }
}
