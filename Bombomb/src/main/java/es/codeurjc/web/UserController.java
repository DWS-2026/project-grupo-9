package es.codeurjc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/profile")
	public String profile(Model model) {
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


