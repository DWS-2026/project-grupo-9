package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.Entity;


@Entity
public class Box extends Product {
    private int size=9;

    public Box(String name, String price, String description, String stock, Blob image, String type){
        super(name, price, description, stock, image, "Box");
    }

}
