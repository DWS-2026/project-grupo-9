package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}