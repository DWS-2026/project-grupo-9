package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
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
import es.codeurjc.web.model.Order;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.OrderService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user", actualUser);
		List<Order> closedOrders = orderService.findClosedOrdersByUserEmail(actualUser.getEmail());
		model.addAttribute("closedOrders", closedOrders);
		return "profilePage";
	}

	@GetMapping("/profile/{id}/image")
	public ResponseEntity<Object> downloadProfileImageAdminList(@PathVariable long id) throws SQLException {
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

	@GetMapping("/profile/image")
	public ResponseEntity<Object> downloadProfileImage(HttpServletRequest request) throws SQLException {
		Optional<User> op = userRepository.findByEmail(request.getUserPrincipal().getName());

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
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("user", user);
		return "editProfile";
	}

	/*
	 * @PostMapping("/editprofile")
	 * public String editProfile(Model model, HttpServletRequest request){
	 * User actualUser =
	 * userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow(
	 * );
	 * model.addAttribute("user",actualUser);
	 * model.addAttribute("name", actualUser.getName());
	 * model.addAttribute("surname", actualUser.getSurname());
	 * model.addAttribute("image", actualUser.getImage());
	 * return "editProfile";
	 * }
	 */
	@PostMapping("/editprofile")
	public String editProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String name,
			@RequestParam(required = false) String telephone, @RequestParam(required = false) String surname,
			 @RequestParam(required = false) MultipartFile imageFile)
			throws IOException {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if (name != null) {
			actualUser.setName(name);
		}
		if (telephone != null) {
			actualUser.setTelephone(telephone);
		}
		if (surname != null) {
			actualUser.setSurname(surname);
		}
		
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				actualUser.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		userRepository.save(actualUser);
		model.addAttribute("user", actualUser);
		model.addAttribute("name", actualUser.getName());
		model.addAttribute("telephone", actualUser.getTelephone());
		model.addAttribute("surname", actualUser.getSurname());
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
		// model.addAttribute("username", request.getUserPrincipal().getName());
		// model.addAttribute("admin", request.isUserInRole("ADMIN"));
		return "userList";
	}

	@GetMapping("/userList/{id}")
	public String viewUser(Model model, @PathVariable long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("admin", true);
		model.addAttribute("user", user);
		return "profilePage";
	}

	@PostMapping("/delete/profile")
	public String deleteUser(Model model, HttpServletRequest request) {
		User actualUser = userRepository.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		userRepository.delete(actualUser);
		return "redirect:/logout";
	}

	@PostMapping("/delete/{id}/profile")
	public String deleteUserForAdmin(Model model, @PathVariable long id) {
		User actualUser = userRepository.findById(id).orElseThrow();
		userRepository.delete(actualUser);
		return "redirect:/userList";
	}

	@PostMapping("/signin")
	public String newUser(Model model, User user, String password, MultipartFile imageFile) throws IOException {
		if (!imageFile.isEmpty()) {
			try {
				user.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		if(!userService.minPasswordLength(password)){
			model.addAttribute("message", "La contraseña no tiene 8 caracteres");
			return "error";
		}
		userService.save(user, password);
		return "redirect:/login";
	}

}
