package es.codeurjc.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import java.util.List;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User user;

    @ManyToMany
    private List<Box> boxes;

    private LocalDate date;
    private float price;
    private int amount;
    private boolean isOpen;

    protected Order() {
    }

    public Order(LocalDate date, float price, int amount, boolean isOpen) {
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.isOpen = isOpen;
    }

    public Order(boolean isOpen) {
        this.isOpen = isOpen; 
        this.date = LocalDate.now();  
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format(
                "Order[id=%d, date='%s', price='.2%f', amount='%d', isOpen='%b']",
                id, date.format(dateFormat), price, amount, isOpen);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void addBox(Box box) {
        this.boxes.add(box);
    }

    public void setBoxes(List<Box> products) {
        this.boxes = products;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateCart(){
        setAmount(this.getBoxes().size());
        setPrice(updatePrice());
    }

    public float updatePrice(){
        float total = 0;
        for (Box box : boxes){
            total = total + box.getPrice();
        }
        return total;
    }

    public void removeBox(Box box){
        boxes.remove(box);
        this.updateCart();
    }

}