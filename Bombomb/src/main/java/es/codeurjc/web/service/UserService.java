package es.codeurjc.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;   

     public void save(User user, MultipartFile image) throws IOException{
        if (!image.isEmpty()) {
			try {
				user.setImage(new SerialBlob(image.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
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
}
