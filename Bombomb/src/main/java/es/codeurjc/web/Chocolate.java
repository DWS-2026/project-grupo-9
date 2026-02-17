package es.codeurjc.web;

import org.springframework.stereotype.Indexed;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Chocolate extends Product{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    protected Chocolate(){}

    public Chocolate(String name, String price, String description, String stock, String image) {
        super(name, price, description, stock, image, "Chocolate");
    }
}
