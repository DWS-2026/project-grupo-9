package es.codeurjc.web;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChocolateRepository extends JpaRepository<Chocolate, Long>{
    
}