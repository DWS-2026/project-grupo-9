package es.codeurjc.web.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SuccessController {
    

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

	@GetMapping("/error/login")
	public String incorrectPass(Model model) {
		model.addAttribute("message", "Contraseña o email incorrecto");
		return "error";
	}
    @GetMapping("/error/minPassword")
	public String minPassword(Model model){
		model.addAttribute("message", "La contraseña debe tener al menos 8 caracteres");
		return "error";
	}
	
}
