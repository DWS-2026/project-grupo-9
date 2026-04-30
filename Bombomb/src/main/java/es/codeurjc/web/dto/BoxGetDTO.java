package es.codeurjc.web.dto;

import java.util.List;

public record BoxGetDTO(Long id, String name, Float price, Boolean madeByAdmin, Boolean isOpenBox, List<ChocolateDTO> chocolates, ImageDTO image) {
    
}
/*
 @ManyToMany(mappedBy="boxes")
private List<Order> orders;
*/
