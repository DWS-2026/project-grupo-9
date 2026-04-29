package es.codeurjc.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;



    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public void deleteById(long id) {
        orderRepository.deleteById(id);
    }

    public Order getActiveCart() {
        return orderRepository.findAll().stream().filter(order -> order.isOpen()).findFirst().get();
    }

    public List<Order> findByIsOpen(Boolean isOpen) {
        return orderRepository.findByIsOpen(isOpen);
    }

    public List<Order> findByUserEmailAndIsOpen(String Email, Boolean isOpen) {
        return orderRepository.findByUserEmailAndIsOpen(Email, isOpen);
    }

    public List<Order> findClosedOrdersByUserEmail(String userEmail) {
        return orderRepository.findByUserEmailAndIsOpen(userEmail, false);
    }

    public List<Order>findByIdAndIsOpen(long id, Boolean isOpen){
        return orderRepository.findByIdAndIsOpen(id, isOpen);
    }

    public List<Order> findByUserEmail(String userEmail){
        return orderRepository.findByUserEmail(userEmail);
    }

    public Order createNewCart(String userEmail) {

        Order newCart = new Order(LocalDate.now(), 0.0f, 0, true);
        newCart.setBoxes(new ArrayList<>());
        newCart.setUser(userService.findByEmail(userEmail).get());
        return orderRepository.save(newCart);
    }

    public void addBoxToCart(String userEmail, Box box) {
        Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
        if(isBoxInCart(userEmail, box.getId())==false){
            cart.addBox(box);      
        }
        cart.updateCart();
        orderRepository.save(cart);
    }

    public void closeTheCart(String userEmail) {
        Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
        cart.setOpen(false);
        orderRepository.save(cart);
    }

    public boolean isBoxInCart(String userEmail, long boxId) {
        Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
        return cart.getBoxes().stream().anyMatch(box -> box.getId() == boxId);
    }

    public void removeBoxFromCart(String userEmail, long boxId) {
        Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
        List<Box> boxes = cart.getBoxes();
        boxes.removeIf(box -> box.getId() == boxId);
        cart.updateCart();
        orderRepository.save(cart);
    }

    public List<Order> findByBoxesAndIsOpen(Box box, Boolean isOpen){
        return orderRepository.findByBoxesAndIsOpen(box, isOpen);
    }

    public boolean hasPermisionToSee(String userEmail, long orderId){
        Order cart = orderRepository.findByIdAndIsOpen(orderId, true).stream().findFirst().orElseThrow();
        User user = userService.findByEmail(userEmail).get();
        if (cart == null) {
			return false;
		} else if (cart.getUser().getEmail().equals(userEmail) || user.isThisRole("ADMIN")) {
            return true;
		}
        return false;
    }

}
