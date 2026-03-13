package es.codeurjc.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.repository.OrderRepository;
import jakarta.annotation.PostConstruct;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;


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
        return orderRepository.findAll().stream().filter(order -> order.isOpen()).findFirst()
                .orElse(createNewCart());
    }

    public List<Order> findByUserEmailAndIsOpen(String Email, Boolean isOpen) {
        return orderRepository.findByUserEmailAndIsOpen(Email, isOpen);
    }

    private Order createNewCart() {
        Order newCart = new Order(LocalDate.now(), 0.0f, 0, true);
        newCart.setBoxes(new ArrayList<>());
        return orderRepository.save(newCart);
    }

    public void addBoxToCart(String userEmail, Box box) {
        Order cart = orderRepository.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();
        List<Box> boxes = cart.getBoxes();
        boxes.add(box);
        cart.setAmount(boxes.size());
        orderRepository.save(cart);
    }

}
