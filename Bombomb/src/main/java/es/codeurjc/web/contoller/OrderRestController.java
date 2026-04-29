package es.codeurjc.web.contoller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.User;
import es.codeurjc.web.dto.OrderDTO;
import es.codeurjc.web.dto.OrderMapper;
import es.codeurjc.web.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.web.service.BoxService;
import es.codeurjc.web.service.UserService;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/v1/orders")

public class OrderRestController {
    @Autowired
	private OrderService orderService;

	@Autowired
	private BoxService boxService;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserService userService;


	@GetMapping("/")
	public Collection<OrderDTO> getOrders(@RequestParam(required = false) Boolean isOpen, HttpServletRequest request) {
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if (isOpen == null) {
			if(user.isThisRole("ADMIN")){
				return orderMapper.toDTOs(orderService.findAll());
			}else{
				return orderMapper.toDTOs(orderService.findByUserEmail(request.getUserPrincipal().getName()));
			}
		}else if(!user.isThisRole("ADMIN")){
			return orderMapper.toDTOs(orderService.findByUserEmailAndIsOpen(user.getEmail(), isOpen));
		}
		return orderMapper.toDTOs(orderService.findByIsOpen(isOpen));
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderDTO> getOrder(@PathVariable long id, HttpServletRequest request) {
		if(orderService.hasPermisionToSee(request.getUserPrincipal().getName(), id)){
			return ResponseEntity.ok(orderMapper.toDTO(orderService.findById(id).orElseThrow()));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
//We don't need a URL to create a new cart because to get a new cart you must have had closed the one you have already open
//so the closing function creates the cart, preventing any chance to have more than one open cart or creating one for someone.
	@PostMapping("/")
	public ResponseEntity<OrderDTO> closeOrder(HttpServletRequest request) { 
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		
		Order order = orderService.findByUserEmailAndIsOpen(user.getEmail(), true).stream().findFirst().orElseThrow();
		orderService.closeTheCart(user.getEmail());
		orderService.createNewCart(user.getEmail());
		return ResponseEntity.ok(orderMapper.toDTO(order));
		
	}

	@PutMapping("/{id}/boxes/{boxId}") 
	public ResponseEntity<OrderDTO> addBoxToCart(@PathVariable long id, @PathVariable long boxId, HttpServletRequest request) {
		Order cart = orderService.findByIdAndIsOpen(id, true).stream().findFirst().get();

		if (orderService.hasPermisionToSee(request.getUserPrincipal().getName(), id) && request.getUserPrincipal().getName().equals(cart.getUser().getEmail())) {
			Box box = boxService.findByIdAndIsAvailableAndMadeByAdmin(boxId, true, true).orElseThrow();
			orderService.addBoxToCart(cart.getUser().getEmail(), box);
			Order updatedCart = orderService.findByIdAndIsOpen(id, true).stream().findFirst().orElseThrow();
			return ResponseEntity.ok(orderMapper.toDTO(updatedCart));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@DeleteMapping("/{id}/boxes/{boxId}") 
	public ResponseEntity<OrderDTO> removeBoxFromCart(@PathVariable long id, @PathVariable long boxId, HttpServletRequest request) {
		Order cart = orderService.findByIdAndIsOpen(id, true).stream().findFirst().get();
		
		if (orderService.hasPermisionToSee(request.getUserPrincipal().getName(), id) && request.getUserPrincipal().getName().equals(cart.getUser().getEmail())){
			orderService.removeBoxFromCart(request.getUserPrincipal().getName(), boxId);
			Order updatedCart = orderService.findByIdAndIsOpen(id, true).stream().findFirst().get();
			return ResponseEntity.ok(orderMapper.toDTO(updatedCart));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

}