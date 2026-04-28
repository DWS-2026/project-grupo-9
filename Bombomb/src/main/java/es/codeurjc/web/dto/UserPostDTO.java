
package es.codeurjc.web.dto;

public record UserPostDTO(Long id, String name, String surname, String telephone, String email, ImageDTO image, String description, String password) {
    
}

