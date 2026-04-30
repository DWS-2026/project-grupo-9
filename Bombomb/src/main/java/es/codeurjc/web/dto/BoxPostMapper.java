package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.Box;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BoxPostMapper {

    BoxPostDTO toDTO(Box box);

    List<BoxPostDTO> toDTOs(Collection<Box> boxes);

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "price", ignore = true)

    Box toDomain(BoxPostDTO boxPostDTO);
}
