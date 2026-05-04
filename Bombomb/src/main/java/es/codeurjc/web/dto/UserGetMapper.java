package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserGetMapper {

    UserGetDTO toDTO(User user);

    List<UserGetDTO> toDTOs(Collection<User> users);

    @Mapping (target="image", ignore=true)
    //@Mapping (target="orders", ignore=true)
    User toDomain(UserGetDTO userGetDTO);

}
