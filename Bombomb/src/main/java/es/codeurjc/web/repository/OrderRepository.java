package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
}
