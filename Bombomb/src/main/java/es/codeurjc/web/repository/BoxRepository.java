package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.Box;

public interface BoxRepository extends JpaRepository<Box, Long>{

    //Optional<Box> findByIsOpenBoxAndOrdersIsOpenAndOrdersUserEmail(Boolean isOpenBox, Boolean isOpen, String email);
    @Query("""
    SELECT b
    FROM Box b
    JOIN b.orders o
    JOIN o.user u
    WHERE b.isOpenBox = :isOpenBox
      AND o.isOpen = :isOpen
      AND u.email = :email
""")
Optional<Box> findBoxByStatusAndUserEmail(
        @Param("isOpenBox") Boolean isOpenBox,
        @Param("isOpen") Boolean isOpen,
        @Param("email") String email
);
        List <Box> findByChocolatesId(long id);
}
