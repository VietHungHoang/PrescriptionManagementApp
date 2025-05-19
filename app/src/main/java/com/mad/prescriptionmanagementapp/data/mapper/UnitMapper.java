package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.Unit;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.UnitResponse;

import org.modelmapper.ModelMapper;

public class UnitMapper {
    private static final ModelMapper modelMapper = new ModelMapper();
    public static Unit responseToModel(UnitResponse unitResponse) {
        return modelMapper.map(unitResponse, Unit.class);
    }

    public static UnitEntity responseToEntity(UnitResponse unitResponse) {
        return modelMapper.map(unitResponse, UnitEntity.class);
    }

    public static UnitResponse modelToResponse(Unit unit) {
        return modelMapper.map(unit, UnitResponse.class);
    }

    public static Unit entityToModel(UnitEntity unitEntity) {
        return modelMapper.map(unitEntity, Unit.class);
    }
}
