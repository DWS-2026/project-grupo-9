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
public class Box {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private final int size=9;
    private String name;
    private float price;
    private Boolean madeByAdmin;
    private Boolean isOpenBox=false;
    private Boolean isAvailable = true;
    @ManyToMany
    private List<Chocolate> chocolates;
    @ManyToMany(mappedBy="boxes")
    private List<Order> orders;
    @Lob
    Blob image;

    public Box() {
        
    }
    

    public Box(String name, float price, Blob image, Boolean madeByAdmin, List<Chocolate> chocolates) {
        this.name=name;
        this.price= price;
        this.image= image;
        this.madeByAdmin= madeByAdmin;
        this.chocolates = chocolates;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }


    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }


    public List<Order> getOrders() {
        return orders;
    }


    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

      
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public Boolean getMadeByAdmin() {
        return madeByAdmin;
    }
    public void setMadeByAdmin(Boolean madeByAdmin) {
        this.madeByAdmin = madeByAdmin;
    }
    public Boolean getIsOpenBox() {
        return isOpenBox;
    }
       public void setIsOpenBox(Boolean isOpenBox) {
        this.isOpenBox = isOpenBox;
    }
    public List<Chocolate> getChocolates() {
        return chocolates;
    }
    public void setChocolates(List<Chocolate> chocolates) {
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
    public int getSize() {
        return size;
    }

    public long getId() {
        return id;
    }
    
    
}
