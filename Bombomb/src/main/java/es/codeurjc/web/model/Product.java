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

    protected Product() {
    }

    public Product(String name, String price, String description, Blob image, String type, boolean madeByAdmin) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.imageFile = image;
        this.madeByAdmin = madeByAdmin;
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%d, name='%s', price='%s', description='%s', type='%s', madeByAdmin='%b']",
                id, name, price, description, type, madeByAdmin);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getMadeByAdmin() {
        return madeByAdmin;
    }

    public void setMadeByAdmin(boolean madeByAdmin) {
        this.madeByAdmin = madeByAdmin;
    }

}
