package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPostMapper {

    UserPostDTO toDTO(User user);

    List<UserPostDTO> toDTOs(Collection<User> users);

    @Mapping (target="image", ignore=true)
    User toDomain(UserPostDTO userPostDTO);

}