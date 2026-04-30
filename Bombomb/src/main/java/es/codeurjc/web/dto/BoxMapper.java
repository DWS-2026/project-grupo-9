package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.Box;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BoxMapper {

    BoxDTO toDTO(Box box);

    List<BoxDTO> toDTOs(Collection<Box> boxes);

    @Mapping(target = "image", ignore = true)

    Box toDomain(BoxDTO boxDTO);
}
