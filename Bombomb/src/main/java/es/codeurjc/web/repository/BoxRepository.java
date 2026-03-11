package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Box;

public interface BoxRepository extends JpaRepository<Box, Long>{
    
}
