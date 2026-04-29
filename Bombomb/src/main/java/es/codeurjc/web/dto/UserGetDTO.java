package es.codeurjc.web.dto;
import java.util.List;
//use por GET,DELETE and PUT, because they dont have password
public record UserGetDTO(Long id, String name, String surname, String telephone, String email, ImageDTO image, String description, List<OrderDTO> orders) {
    
}
