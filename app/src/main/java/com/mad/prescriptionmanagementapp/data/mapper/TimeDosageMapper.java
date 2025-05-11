package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class TimeDosageMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static TimeDosageEntity modelToEntity(TimeDosage timeDosage) {
        return modelMapper.map(timeDosage, TimeDosageEntity.class);
    }
}
