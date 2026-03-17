package es.codeurjc.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.repository.OrderRepository;
import jakarta.annotation.PostConstruct;

@Service
public class OrderService {
    private final UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    OrderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {

    }

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

    public List<Order> findByUserEmailAndIsOpen(String Email, Boolean isOpen) {
        return orderRepository.findByUserEmailAndIsOpen(Email, isOpen);
    }

    public List<Order> findClosedOrdersByUserEmail(String userEmail) {
        return orderRepository.findByUserEmailAndIsOpen(userEmail, false);
    }

    public Order createNewCart(String userEmail) {

        Order newCart = new Order(LocalDate.now(), 0.0f, 0, true);
        newCart.setBoxes(new ArrayList<>());
        newCart.setUser(userRepository.findByEmail(userEmail).get());
        return orderRepository.save(newCart);
    }

    public void addBoxToCart(String userEmail, Box box) {
        if(isBoxInCart(userEmail, box.getId())==false){
            Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
            if(box.getMadeByAdmin()!= false){
                List<Box> boxes = cart.getBoxes();
                boxes.add(box);
            }
            cart.updateCart();
            orderRepository.save(cart);
        }
        
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

}
