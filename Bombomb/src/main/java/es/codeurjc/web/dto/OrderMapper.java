package es.codeurjc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.web.model.Order;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    OrderDTO toDTO(Order order);

    @Mapping(target = "open", source = "isOpen")
    @Mapping(target = "boxes", ignore = true)
    @Mapping(target = "user", ignore = true)
    Order toDomain(OrderDTO orderDTO);

    List<OrderDTO> toDTOs(Collection<Order> orders);

}