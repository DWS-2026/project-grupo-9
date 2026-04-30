package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class File {
   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String originalName;
  
    private String name;

    @ManyToOne
    private User user; //email of the user that uploaded the file

    public File() {
    }

    public File(String originalName, User user) {
        this.originalName = originalName;
        
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }
      public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

}
