package es.codeurjc.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String date;
    private String price;
    private int amount;
    private boolean status;

    protected Order() {
    }

    public Order(String date, String price, int amount, boolean status) {
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "Order[id=%d, date='%s', price='%s', description='%s', amount='%d', status='%d']",
                id, date, price, amount, status);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStock(boolean status) {
        this.status = status;
    }

}