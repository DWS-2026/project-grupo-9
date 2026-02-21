package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Chocolate;

public interface ChocolateRepository extends JpaRepository<Chocolate, Long>{
    
}