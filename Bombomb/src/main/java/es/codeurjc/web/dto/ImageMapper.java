package es.codeurjc.web.dto;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}
