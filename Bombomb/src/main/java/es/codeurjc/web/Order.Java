package es.codeurjc;

import org.springframework.stereotype.Indexed;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String date;
    private String price;
    private int amount;
    private boolean status;


    protected Order() {}   

    public Order(String date, String price, int amount, boolean status) {
        this.name = date;
        this.price = price;
        this.description = description;
        this.amount = amount;
        this.status = status;
        private HashMap<Product, int> productsMap = new HashMap<Product, int>();
    }

    @Override
    public String toString() {
        return String.format(
                "Order[id=%d, date='%s', price='%s', description='%s', amount='%d', status='%d']",
                id, date, price, description, amount, status);
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

    public boolean getStatus() {
        return status;
    }

    public void setStock(boolean status) {
        this.status = status;
    }

    public HashMap<Product, int> getProductsMap() {
        return productsMap;
    }

    public void setProductsMap(HashMap<Product, int> productsMap) {
        this.productsMap = productsMap;
    }

}