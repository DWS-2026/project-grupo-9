package es.codeurjc.web.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Indexed;

import java.sql.Blob;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Admin extends User {
    
    @Id
    @GeneratedValue( strategy =GenerationType.AUTO)
    private long id;

    @OneToMany
	private List<Product> products;

    private String password="admin";

    public Admin(String name, String surname, String telephone, String email, Blob image, String password){
        super(name,surname,telephone, email, image, password);
        this.products = new ArrayList<>();
    }

    public Admin() {
        super();
    }
   
    public boolean isAdmin(String password){
        if(password.equals(this.password)){
            return true;
        }
        return false;
    }

     public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
