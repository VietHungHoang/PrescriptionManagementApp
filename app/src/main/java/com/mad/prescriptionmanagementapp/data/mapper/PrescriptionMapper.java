package com.mad.prescriptionmanagementapp.data.mapper;

import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.remote.dto.request.PrescriptionRequest;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PrescriptionMapper {
    private static ModelMapper modelMapper;

    static {

        Converter<String, LocalDate> toLocalDate = context -> {
            if (context.getSource() != null) {
                return LocalDate.parse(context.getSource(), DateTimeFormatter.ISO_DATE);
            }
            return null;
        };

        modelMapper.addConverter(toLocalDate, String.class, LocalDate.class);

        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<PrescriptionRequest, PrescriptionEntity>() {
            @Override
            protected void configure() {
                // Ánh xạ tên bác sĩ từ PrescriptionRequest sang PrescriptionEntity
                map(source.getDoctorName(), destination.getDoctorName());
            }
        });
    }

    public static PrescriptionEntity modelToEntity(PrescriptionRequest prescriptionRequest) {
        return modelMapper.map(prescriptionRequest, PrescriptionEntity.class);
    }
}
