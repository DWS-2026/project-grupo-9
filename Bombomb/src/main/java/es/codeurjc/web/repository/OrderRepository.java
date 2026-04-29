package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    

    List<Order> findByUserEmailAndIsOpen(String Email, Boolean isOpen);
   List <Order> findByBoxesAndIsOpen(Box box, Boolean isOpen);
   List<Order> findByIsOpen(Boolean isOpen);
   List<Order> findByIdAndIsOpen(long id, Boolean isOpen);
   List<Order> findByUserEmail(String userEmail);

}
