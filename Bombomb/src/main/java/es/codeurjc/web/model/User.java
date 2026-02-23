package es.codeurjc.web.model;

import java.sql.Blob;
import org.springframework.stereotype.Indexed;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String surname;
    private String telephone;
    private String email;
    private String password;
    @Lob
    private Blob image;

    protected User() {
    }

    public User(String name, String surname, String telephone, String email, Blob image, String password) {

        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
        this.email = email;
        this.image = image;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', surname='%s', telephone='%s', email='%s', password='%s']",
                id, name, surname, telephone, email, password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getPassword() {/* no se hasta que punto esta funcion es buena idea */
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
