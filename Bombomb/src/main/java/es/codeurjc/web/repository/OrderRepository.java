package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.Box;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    

    List<Order> findByUserEmailAndIsOpen(String Email, Boolean isOpen);
    List<Box> findByUserEmailAndIsOpenAndBoxIsOpenBox(String Email, Boolean isOpen, Boolean isOpenBox);


}
