package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Chocolate;
import java.util.List;
import java.util.Optional;


public interface ChocolateRepository extends JpaRepository<Chocolate, Long>{
    List <Chocolate> findByIsAvailable(Boolean isAvailable);
    Optional <Chocolate> findByIdAndIsAvailable(long id, Boolean isAvailable);
}