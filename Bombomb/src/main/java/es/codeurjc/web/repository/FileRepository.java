package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.File;

public interface FileRepository extends JpaRepository<File, Long>{}
