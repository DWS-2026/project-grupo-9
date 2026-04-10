package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.User;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.OrderService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;

	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		User actualUser = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		model.addAttribute("user", actualUser);
		List<Order> closedOrders = orderService.findClosedOrdersByUserEmail(actualUser.getEmail());
		model.addAttribute("closedOrders", closedOrders);
		return "profilePage";
	}

	@GetMapping("/profile/{id}/image")
	public ResponseEntity<Object> downloadProfileImageAdminList(@PathVariable long id) throws SQLException {
		Optional<User> op = userService.findById(id);
		if (op.isPresent() && op.get().getImage() != null) {
			return imageService.getImage(op.get().getImage());
		} else {
			return imageService.getNotFoundImage();
		}
	}

	@GetMapping("/profile/image")
	public ResponseEntity<Object> downloadProfileImage(HttpServletRequest request) throws SQLException {
		Optional<User> op = userService.findByEmail(request.getUserPrincipal().getName());

		if (op.isPresent() && op.get().getImage() != null) {
			return imageService.getImage(op.get().getImage());
		} else {
			return imageService.getNotFoundImage();
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
		User user = userService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("user", user);
		return "editProfile";
	}

	@PostMapping("/editprofile")
	public String editProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String name,
			@RequestParam(required = false) String telephone, @RequestParam(required = false) String surname,
			@RequestParam(required = false) MultipartFile imageFile)
			throws IOException {
		User actualUser = userService.editUserProfile(request.getUserPrincipal().getName(), name, surname, telephone, imageFile);
		
		model.addAttribute("user", actualUser);

		return "editprofile";
	}

	@GetMapping("/payment")
	public String payment(Model model) {
		return "payment";
	}

	@GetMapping("/userList")
	public String userList(Model model) {
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		return "userList";
	}

	@GetMapping("/userList/{id}")
	public String viewUser(Model model, @PathVariable long id) {
		User user = userService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		model.addAttribute("admin", true);
		model.addAttribute("user", user);
		model.addAttribute("closedOrders", orderService.findClosedOrdersByUserEmail(user.getEmail()));
		return "profilePage";
	}

	@PostMapping("/delete/profile")
	public String deleteUser(Model model, HttpServletRequest request) {
		User actualUser = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		userService.delete(actualUser);
		request.getSession().invalidate();
		return "redirect:/";
	}

	@PostMapping("/delete/{id}/profile")
	public String deleteUserForAdmin(Model model, @PathVariable long id, HttpServletRequest request) {
		User actualUser = userService.findById(id).orElseThrow(()-> new IllegalArgumentException("Usuario no encontrado"));
		if(actualUser.isThisRole("ADMIN")){
			request.getSession().invalidate();
		}
		userService.delete(actualUser);
		return "redirect:/userList";
	}

	@PostMapping("/signin")
	public String newUser(Model model, User user, MultipartFile imageFile,HttpServletRequest request, @RequestParam String password) throws IOException {
		if (!imageFile.isEmpty()) {
			try {
				user.setImage(new SerialBlob(imageFile.getBytes()));//userService.setImage(user, imageFile)
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		if(!userService.minPasswordLength(password)){
			return "redirect:/error/minPassword";
		}
		userService.save(user, password);
		return "redirect:/login";
	}

}
