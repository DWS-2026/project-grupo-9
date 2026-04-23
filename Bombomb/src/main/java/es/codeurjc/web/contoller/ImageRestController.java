package es.codeurjc.web.contoller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.ImageDTO;
import es.codeurjc.web.dto.ImageMapper;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private ImageMapper mapper;

    @Autowired
    ImageService imageService;

    @Autowired
    UserService userService;

    /*
     * @GetMapping("/")
     * public Collection<ImageDTO> getImages() {
     * return mapper.toDTOs(imageService.findAll());
     * }
     */

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Image image = imageService.findById(id).orElseThrow();
        if (imageService.hasPermision(principal, image)) {
            return ResponseEntity.ok(mapper.toDTO(image));
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping("/{id}/media")
    public ResponseEntity<Object> downloadImage(@PathVariable long id, HttpServletRequest request) throws SQLException {
        Principal principal = request.getUserPrincipal();
        Image image = imageService.findById(id).orElseThrow();
        if(imageService.hasPermision(principal, image)){
            return imageService.getImage(image);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }        
    }

    @PutMapping(value = "/{id}/media", consumes = "multipart/form-data")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
            @RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws IOException, SerialException, SQLException {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Principal principal = request.getUserPrincipal();
        Image image = imageService.findById(id).orElseThrow();
        if(imageService.hasEditPermision(principal, image)){
            imageService.replaceImage(id, imageFile);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
