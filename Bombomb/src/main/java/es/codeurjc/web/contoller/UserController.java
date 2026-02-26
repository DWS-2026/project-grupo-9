package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.RepositoryUserDetailsService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	

	/**@PostConstruct
	private void initDatabase() throws IOException, SerialException, SQLException {
		ClassPathResource resource = new ClassPathResource("static/images/chocolate_flower.jpeg");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		userRepository.save(new User("María de la O", "Sánchez Sánchez",
				"600808080", "mariasanchezsanchez@hotmail.com", blob, 
				passwordEncoder.encode("pass"), "USER"));

		resource = new ClassPathResource("static/images/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		userRepository.save(new User("Administrador", "Adminis Trado",
				"666 666 666", "administrador@gmail.com", blob, 
				passwordEncoder.encode("adminpass"), "USER", "ADMIN"));
	}**/

	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user",actualUser);
		/**model.addAttribute("name", "María de la O ");
		model.addAttribute("surname", "Sánchez Sánchez");
		model.addAttribute("telephone", "+34 600 808080");
		model.addAttribute("email", "mariasanchezsanchez@hotmail.com");
		model.addAttribute("image",
				"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTErNULijVAz9MIn0j-zc0bkiiSmoFrXnIATg&s");
				**/
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

	@GetMapping("/editprofile/{id}")
	public String editProfile(Model model, @PathVariable long id) {
		User user = userRepository.getById(id);
		model.addAttribute(user);
		return "editProfile";
	}

	@GetMapping("/payment")
	public String payment(Model model) {
		return "payment";
	}

	@GetMapping("/userList")
	public String userList(Model model) {
		model.addAttribute("name", "María de la O Sánchez Sánchez");
		model.addAttribute("email", "mariasanchezsanchez@hotmail.com");
		model.addAttribute("telephone", "+34 600808080");
		model.addAttribute("image",
				"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTErNULijVAz9MIn0j-zc0bkiiSmoFrXnIATg&s");
		return "userList";
	}

	
	@PostMapping("/signin")
	public String newUser(Model model, User user, String password, MultipartFile imageFile) throws IOException {
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
	
	@PostMapping("/delete/profile")
	public String deleteUser(Model model, HttpServletRequest request) {
		//TODO: process POST request
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		userRepository.delete(actualUser);
		return "redirect:/login";
	}
	
	
}

