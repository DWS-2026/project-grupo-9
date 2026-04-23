package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private Blob blobImage;

    private String owner;//This is going to be or "public" or the email of the owner of the object that has this image
    //If owner is "public" the image is public
    //If owner is an email, it wil be available for the owner and the admin
    //If its the profile image of the admin, owner = "admin@gmail.com", not "public"

    public Image() {
    }

    public Image(Blob blobImage, String owner) {
        this.blobImage = blobImage;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Blob getBlobImage() {
        return blobImage;
    }

    public void setBlobImage(Blob blobImage) {
        this.blobImage = blobImage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
}
