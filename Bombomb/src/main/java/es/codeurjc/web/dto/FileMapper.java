package es.codeurjc.web.dto;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.File;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileDTO toDTO(File file);

    Collection<FileDTO> toDTOs(Collection<File> files);

    File toDomain(FileDTO fileDTO);
}