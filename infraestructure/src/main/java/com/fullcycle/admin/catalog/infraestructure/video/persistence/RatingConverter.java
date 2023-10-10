package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.video.Rating;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {
    @Override
    public String convertToDatabaseColumn(Rating attribute) {
        if(attribute == null) return null;
        return attribute.getName();
    }

    @Override
    public Rating convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        return Rating.of(dbData).orElse(null);
    }
}
