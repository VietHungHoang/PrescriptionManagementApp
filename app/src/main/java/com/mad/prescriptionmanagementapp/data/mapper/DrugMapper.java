package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.cache.DrugCache;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.DrugResponse;

import org.modelmapper.ModelMapper;

public class DrugMapper {
    private static ModelMapper modelMapper = new ModelMapper();
    public static DrugCache responseToCache(DrugResponse drug) {
        return modelMapper.map(drug, DrugCache.class);
    }
    public static DrugResponse cacheToResponse(DrugCache drug) {
        return modelMapper.map(drug, DrugResponse.class);
    }
}
