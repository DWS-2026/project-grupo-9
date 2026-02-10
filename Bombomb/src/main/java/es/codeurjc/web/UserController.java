package main.java.es.codeurjc.web;

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
}


