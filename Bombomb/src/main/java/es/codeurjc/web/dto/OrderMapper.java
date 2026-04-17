package es.codeurjc.web.dto;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Order;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs(Collection<Order> orders);

    Order toDomain(OrderDTO orderDTO);
}