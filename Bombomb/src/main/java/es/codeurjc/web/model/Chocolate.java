package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.Id;

@Entity
public class Chocolate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    String name;

    @Lob
    Blob image;

    protected Chocolate() {
    }

    public Chocolate(String chocolateName, Blob chocolateImage){
        this.name = chocolateName;
        this.image = chocolateImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

}
