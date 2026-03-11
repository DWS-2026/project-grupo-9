package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
    private OrderService orderService;

	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user",actualUser);
		
		model.addAttribute("date", "24/06/2026");
		model.addAttribute("numberProducts", "2");
		model.addAttribute("productImage", "./images/chocolate_pink.jpeg");
		model.addAttribute("productType", "Bombón");
		model.addAttribute("productName", "Mármol de frambuesa");
		model.addAttribute("productPrize", "0.60");
		model.addAttribute("productAmount", "1");
		return "profilePage";
	}

	@GetMapping("/profile/{id}/image")
	public ResponseEntity<Object> downloadChocolateImage(@PathVariable long id) throws SQLException {
		Optional<User> op = userRepository.findById(id);
		if (op.isPresent() && op.get().getImage() != null) {
			Blob image = op.get().getImage();
			InputStreamResource imageFile = new InputStreamResource(image.getBinaryStream());
			MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
			return ResponseEntity.ok().contentType(mediaType).body(imageFile);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/login")
	public String logIn(Model model) {
		
		return "logInPage";
	}

	@GetMapping("/signin")
	public String signIn(Model model) {
		return "signInPage";
	}

	@GetMapping("/editprofile")
	public String editProfile(Model model, @PathVariable long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("user",user);
		return "editProfile";
	}
	/* 
	@PostMapping("/editprofile")
	public String editProfile(Model model, HttpServletRequest request){
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user",actualUser);
		model.addAttribute("name", actualUser.getName());
		model.addAttribute("surname", actualUser.getSurname());
		model.addAttribute("image", actualUser.getImage());
		return "editProfile";	
	}
*/
@PostMapping("/editprofile")
	public String editProfile(Model model, HttpServletRequest request,@RequestParam(required = false) String name,@RequestParam(required = false) String telephone,@RequestParam(required = false) String surname, @RequestParam(required = false) String email, @RequestParam(required = false) MultipartFile imageFile) throws IOException {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(name!=null) {
			actualUser.setName(name);
		}
		if(telephone!=null) {
			actualUser.setTelephone(telephone);
		}
		if(surname!=null) {
			actualUser.setSurname(surname);
		}
		if(email!=null) {
			actualUser.setEmail(email);
		}
		if(imageFile!=null && !imageFile.isEmpty()) {
			try {
				actualUser.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		userRepository.save(actualUser);
		model.addAttribute("user",actualUser);
		model.addAttribute("name", actualUser.getName());
	    model.addAttribute("telephone", actualUser.getTelephone());
		model.addAttribute("surname", actualUser.getSurname());
		model.addAttribute("email", actualUser.getEmail());
		model.addAttribute("image", actualUser.getImage());

		return "editprofile";	
	}
	

	@GetMapping("/payment")
	public String payment(Model model) {
		return "payment";
	}

	@GetMapping("/userList")
	public String userList(Model model) {
		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "userList";
	}
	@GetMapping("/userList/{id}")
	public String viewUser(Model model, @PathVariable long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("user", user);
		model.addAttribute("date", "24/06/2026");
		model.addAttribute("numberProducts", "2");
		model.addAttribute("productImage", "./images/chocolate_pink.jpeg");
		model.addAttribute("productType", "Bombón");
		model.addAttribute("productName", "Mármol de frambuesa");
		model.addAttribute("productPrize", "0.60");
		model.addAttribute("productAmount", "1");
		return "profilePage";
	}
	
	@PostMapping("/delete/profile")
	public String deleteUser(Model model, HttpServletRequest request) {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		userRepository.delete(actualUser);
		return "redirect:/logout";
	}
	@PostMapping("/signin")
	public String newChocolate(Model model, User user, String password, MultipartFile imageFile) throws IOException {
		user.setEncodedPassword(passwordEncoder.encode(password));
 		if (!imageFile.isEmpty()) {
			try {
				user.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		userRepository.save(user);
		return "redirect:/profile";
	}
	
	
}

