package es.codeurjc.web.contoller;

import es.codeurjc.web.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.dto.ImageDTO;
import es.codeurjc.web.dto.ImageMapper;
import es.codeurjc.web.dto.UserGetDTO;
import es.codeurjc.web.dto.UserGetMapper;
import es.codeurjc.web.dto.UserPostDTO;
import es.codeurjc.web.dto.UserPostMapper;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGetMapper mapper;

    @Autowired
    private UserPostMapper mapperPost;

    @Autowired
    private ImageMapper imageMapper;

    UserRestController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/")
    public Collection<UserGetDTO> getAllUsers() {
        return mapper.toDTOs(userService.findAll());
    }

    /*@GetMapping("/")
    public Collection<UserGetDTO> getAllUsers(HttpServletRequest request) {
        if(request.isUserInRole("ADMIN")){
            return mapper.toDTOs(userService.findAll()); 
        }else{
            return mapper.toDTOs(userService.findByEmail(request.getUserPrincipal().getName()).stream().toList());
        }
    }*/


    
    @GetMapping("/{id}")
    public ResponseEntity<UserGetDTO> getUser(@PathVariable long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        User user = userService.findById(id).orElseThrow();
               
        if (user.getEmail().equals(principal.getName()) || request.isUserInRole("ADMIN")) {
            return ResponseEntity.ok(mapper.toDTO(user));
        }else{
 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }
   

    
    //if the user is not admin, only can delete his own profile, if is admin can delete to all users profiles   
    @DeleteMapping("/{id}")
    public ResponseEntity<UserGetDTO> deleteUser(@PathVariable long id, HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();
        User user = userService.findById(id).orElseThrow();
        if (user.getEmail().equals(principal.getName()) || request.isUserInRole("ADMIN")) {
            request.getSession().invalidate();
            userService.delete(user);
            return ResponseEntity.ok().build();
        }else{
 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<UserGetDTO> createUser(@RequestBody UserPostDTO user) throws IOException {
        
        if(!userService.isEmailUnique(user.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userService.minPasswordLength(user.password())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } 
        User newuser = mapperPost.toDomain(user);
		userService.save(newuser, user.password(),user.description());
		UserGetDTO responseDTO = mapper.toDTO(newuser);
        URI location = fromCurrentRequest().path("/{id}") .buildAndExpand(responseDTO.id()).toUri();
       
        return ResponseEntity.created(location).body(responseDTO);  
   }
   

    //admin cannot edit other users profiles
    
   @PutMapping("/{id}")
   public ResponseEntity<UserGetDTO> editUser(@PathVariable long id, @RequestBody UserGetDTO updatedUser, HttpServletRequest request) throws IOException, SQLException{
       
        Principal principal = request.getUserPrincipal();
        User actualUser = userService.findById(id).orElseThrow();
        
        if(actualUser.getEmail().equals(principal.getName())){
            userService.updateUser(actualUser,updatedUser);
            return ResponseEntity.ok(mapper.toDTO(actualUser));
        }else{
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        }
    }
    
    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
	public ResponseEntity<ImageDTO> createUserImage(@PathVariable long id,
			@RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException, SerialException, SQLException {
		if (imageFile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
        Principal principal = request.getUserPrincipal();
        User actualUser = userService.findById(id).orElseThrow();
        
        if(actualUser.getEmail().equals(principal.getName())){
            Image image = userService.findById(id).orElseThrow().getImage();
		    if (image.getBlobImage() == null) {//Only add image if it does not have one
			    imageService.replaceImage(image.getId(), imageFile);
		    }
		    URI location = fromCurrentContextPath()
				.path("/images/{imageId}/media")
				.buildAndExpand(image.getId())
				.toUri();
		    return ResponseEntity.created(location).body(imageMapper.toDTO(image));
        }

		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

	}
   
}  
