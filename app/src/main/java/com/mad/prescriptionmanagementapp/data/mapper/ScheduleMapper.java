package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.Schedule;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class ScheduleMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static ScheduleEntity modelToEntity(Schedule schedule) {
        return modelMapper.map(schedule, ScheduleEntity.class);
    }
}
