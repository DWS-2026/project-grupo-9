package es.codeurjc.web.service;

import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.ImageRepository;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.repository.UserRepository;


import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.service.ImageService;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> getImage(Image image) throws SQLException {
        InputStreamResource imageFile = new InputStreamResource(image.getBlobImage().getBinaryStream());
        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

    public ResponseEntity<Object> getNotFoundImage() {
        ClassPathResource notFoundImage = new ClassPathResource("static/images/notFound.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(notFoundImage);
    }

    public List<Image> findAll(){
        return imageRepository.findAll();
    }

    public Optional<Image> findById(long id){
        return imageRepository.findById(id);
    }

    public void replaceImage(long id, MultipartFile imageFile) throws SerialException, SQLException, IOException{
        Image image = imageRepository.findById(id).orElseThrow();
        image.setBlobImage(new SerialBlob(imageFile.getBytes()));
        imageRepository.save(image);
    }

    public Boolean hasPermision(Principal principal, Image image){
        String userEmail = null;
        if(principal != null){
            userEmail = principal.getName();
        }
        String imageOwner = image.getOwner();
        return imageOwner.equals("public") || 
        (userEmail!= null && imageOwner.equals(userEmail)) || 
        (userEmail!= null && userRepository.findByEmail(userEmail).orElseThrow().getRoles().equals("ADMIN"));
    }

    public Boolean hasEditPermision(Principal principal, Image image){
        if(principal == null){
            return false;
        }
        String userEmail = principal.getName();
        String imageOwner = image.getOwner();
        if(imageOwner.equals("public")){
            return userEmail.equals("admin@gmail.com");
        }else{
            return userEmail.equals(imageOwner);
        }
    }
    
}
