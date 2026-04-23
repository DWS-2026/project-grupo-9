package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.Chocolate;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ChocolateMapper {

    ChocolateDTO toDTO(Chocolate chocolate);

    List<ChocolateDTO> toDTOs(Collection<Chocolate> chocolates);

    @Mapping(target = "image", ignore = true)
    Chocolate toDomain(ChocolateDTO chocolateDTO);
}