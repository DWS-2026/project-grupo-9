package es.codeurjc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

     public void save(User user, MultipartFile image,@RequestParam(required = true) String passwordString) throws IOException{
        if (!image.isEmpty()) {
			try {
				user.setImage(new SerialBlob(image.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
        if(minPasswordLength(passwordString)==false) {
            throw new IOException("Password must be at least 8 characters long");
        }
     userRepository.save(user);

    
    }
    //////////////////////////////////////////////
   
   /*  if(user.getRoles().isEmpty()) {
        List<String> cleanRoles = new ArrayList<>();
        for (String role : user.getRoles()) {
            if(role.startsWith("ROLE_")) {
                cleanRoles.add(role.substring(5));
            } else {
                cleanRoles.add(role);
            }
            user.setRoles(cleanRoles);
        }
    }*/

    /// //////////////////////////////////////////////

     public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean minPasswordLength(String password) {
        return password.length() >= 8;
    }
}
