package es.codeurjc.web.dto;

import java.util.List;

public record BoxDTO(long id, String name, float price, Boolean madeByAdmin, Boolean isOpenBox, List<ChocolateDTO> chocolates, ImageDTO image) {
    
}
/*
 @ManyToMany(mappedBy="boxes")
private List<Order> orders;
*/
