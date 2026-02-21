package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String price;
    private String description;
    private String stock;
    private String type;
    private Boolean madeByAdmin;

    @Lob
    private Blob imageFile;

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    protected Product() {}   

    public Product(String name, String price, String description, String stock, Blob image, String type) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.type = type;
        this.imageFile = image;
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%d, name='%s', price='%s', description='%s', stock='%s', type='%s']",
                id, name, price, description, stock, type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
