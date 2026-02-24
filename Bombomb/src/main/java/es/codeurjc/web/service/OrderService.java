package es.codeurjc.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Product;
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
        return orderRepository.findAll().stream().filter(order -> !order.getStatus()).findFirst()
                .orElse(createNewCart());
    }

    private Order createNewCart() {
        Order newCart = new Order(LocalDate.now(), 0.0f, 0, true);
        newCart.setProducts(new ArrayList<>());
        return orderRepository.save(newCart);
    }

    public void addProductToCart(Product product) {
        Order cart = getActiveCart();
        List<Product> products = cart.getProducts();
        products.add(product);
        cart.setAmount(products.size());
        orderRepository.save(cart);
    }

}
