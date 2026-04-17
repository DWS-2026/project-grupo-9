package es.codeurjc.web.contoller;

import java.net.URI;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.dto.OrderDTO;
import es.codeurjc.web.dto.OrderMapper;
import es.codeurjc.web.service.OrderService;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/orders")

public class OrderRestController {
    @Autowired
	private OrderService orderService;

	@Autowired
	private OrderMapper mapper;

	@GetMapping("/")
	public Collection<OrderDTO> getOrders(@RequestParam(required = false) Boolean isOpen) {
        if (isOpen == null) {
            return mapper.toDTOs(orderService.findAll());
        }
		return mapper.toDTOs(orderService.findByIsOpen(isOpen));
	}

	@GetMapping("/{id}/")
	public OrderDTO getOrder(@PathVariable long id) {
		return mapper.toDTO(orderService.findById(id).orElseThrow());
	}
	
}