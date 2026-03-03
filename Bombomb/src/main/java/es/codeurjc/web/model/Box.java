package es.codeurjc.web.model;

import java.sql.Blob;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Id;

@Entity
public class Box extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int size=9;
    private String name;
    private String price;
    private String description;
    private String type;
    private Boolean madeByAdmin;
    @ManyToMany
    private List<Chocolate> chocolates;

    @Lob
    Blob image;
    
    

    public Box(String name, String price, String description, Blob image, String type, Boolean madeByAdmin, List<Chocolate> chocolates) {
        this.name=name;
        this.price= price;
        this.description= description;
        this.image= image;
        this.type= "Box";
        this.madeByAdmin= madeByAdmin;
        this.chocolates = chocolates;
    }
   
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public Blob getImage() { 
        return image;
    }
    public void setImage(Blob image) {
        this.image = image;
    }
    public void setChocolateList(List<Chocolate> chocolates){
        this.chocolates = chocolates;
    }
    public List<Chocolate> getChocolateList(){
        return this.chocolates;
    }
    
}
