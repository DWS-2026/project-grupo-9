package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{}

