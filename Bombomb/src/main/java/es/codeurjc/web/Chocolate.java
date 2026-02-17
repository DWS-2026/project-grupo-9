package es.codeurjc.web;

import org.springframework.stereotype.Indexed;

import jakarta.persistence.Entity;

@Entity
public class Chocolate extends Product{

    protected Chocolate(){}

    public Chocolate(String name, String price, String description, String stock, String image) {
        super(name, price, description, stock, image, "Chocolate");
    }
}
