package es.codeurjc.web.dto;

import java.util.List;

public record BoxPostDTO(Long id, String name) {
    
}
/*
 @ManyToMany(mappedBy="boxes")
private List<Order> orders;
*/
