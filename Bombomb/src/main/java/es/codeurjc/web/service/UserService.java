package es.codeurjc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }   

    public void save(User user, String passwordString) throws IOException{
        if (user.getRoles() == null) {
        	user.setRoles(new ArrayList<>());
    	}
    	user.getRoles().add("USER");
        user.addOrder(new Order(true));
        user.setEncodedPassword(passwordEncoder.encode(passwordString));
        userRepository.save(user);
    }
    
    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }
    public void delete(User user){
        userRepository.delete(user);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
   
    public boolean minPasswordLength(String password) {
        return password != null && password.length() >= 8;
    }
    public User editUserProfile(String email,String name, String surname, String telephone,String description, MultipartFile image) throws IOException {
        User actualUser=userRepository.findByEmail(email).orElseThrow();
        if(name !=null){
            actualUser.setName(name);
        }
        if(surname !=null){
            actualUser.setSurname(surname);
        }
        if(telephone !=null){
            actualUser.setTelephone(telephone);
        }
        if(description !=null){
            actualUser.setDescription(description);
        }
        if (image != null && !image.isEmpty()) {
            try {
                actualUser.setImage(new Image(new SerialBlob(image.getBytes())));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
        }
        return userRepository.save(actualUser);
    }
    public boolean isAdminRole(User user){
        return user.getRoles().contains("ADMIN");
    }

    public void setImage(User user, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            try {
                user.setImage(new Image(new SerialBlob(imageFile.getBytes())));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
        }

    }
    //if email is already in use, return false, otherwise return true
    public boolean isEmailUnique(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }
   
}
