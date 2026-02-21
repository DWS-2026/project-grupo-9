package es.codeurjc.web.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("name", "María de la O ");
		model.addAttribute("surname", "Sánchez Sánchez");
		model.addAttribute("telephone", "+34 600 808080");
		model.addAttribute("email", "mariasanchezsanchez@hotmail.com");
		model.addAttribute("image", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTErNULijVAz9MIn0j-zc0bkiiSmoFrXnIATg&s");
		model.addAttribute("date", "24/06/2026");
		model.addAttribute("numberProducts", "2");
		model.addAttribute("productImage", "./images/chocolate_pink.jpeg");
		model.addAttribute("productType", "Bombón");
		model.addAttribute("productName", "Mármol de frambuesa");
		model.addAttribute("productPrize", "0.60");
		model.addAttribute("productAmount", "1");
		return "profilePage";
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
	public String editProfile(Model model) {
		model.addAttribute("name", "María de la O");
		model.addAttribute("surname", "Sánchez Sánchez");
		model.addAttribute("telephone", "600808080");
		model.addAttribute("email", "mariasanchezsanchez@hotmail.com");
		model.addAttribute("image", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTErNULijVAz9MIn0j-zc0bkiiSmoFrXnIATg&s");
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
		model.addAttribute("image", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTErNULijVAz9MIn0j-zc0bkiiSmoFrXnIATg&s");
		return "userList";
	}
}


