package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.TimeDosage;
import com.mad.prescriptionmanagementapp.data.model.entity.DosageEntity;

import org.modelmapper.ModelMapper;

public class TimeDosageMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static DosageEntity modelToEntity(TimeDosage timeDosage) {
        return modelMapper.map(timeDosage, DosageEntity.class);
    }
}
