package es.codeurjc.web.dto;

import java.util.List;
import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.User;

@Mapper(componentModel = "spring")
public interface UserEmailMapper {

    UserEmailDTO toDTO(User user);

    @Mapping(target = "email", source = "email")
    User toDomain(UserEmailDTO userEmailDTO);

    List<UserEmailDTO> toDTOs(Collection<User> users);
}