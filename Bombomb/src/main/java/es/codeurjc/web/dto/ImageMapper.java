package es.codeurjc.web.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    
    ImageDTO toDTO(Image image);

    List<ImageDTO> toDTOs(Collection<Image> images);
}
