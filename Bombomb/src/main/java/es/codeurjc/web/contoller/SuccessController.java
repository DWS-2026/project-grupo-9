package es.codeurjc.web.contoller;
import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.service.UserService;

@Controller
public class SuccessController {
    
	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

    @GetMapping("/fail")
	public String fail(Model model) {
		return "fail";
	}

    @GetMapping("/error")
	public String error(Model model) {
		return "error";
	}

    @GetMapping("/success")
	public String success(Model model) {
		return "success";
	}

	@GetMapping("/login/error")
	public String incorrectPass(Model model) {
		model.addAttribute("message", "Contraseña o email incorrecto");
		return "error";
	}
	
}
