package es.codeurjc.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Box;

public interface BoxRepository extends JpaRepository<Box, Long>{

    Optional<Box> findByIsOpenBoxAndOrdersIsOpenAndOrdersUserEmail(Boolean isOpenBox, Boolean isOpen,  String email);
}
