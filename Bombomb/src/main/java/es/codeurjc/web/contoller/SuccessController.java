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
		model.addAttribute("message", "Fallo en el pago");
		return "error";
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
	@GetMapping("/error/notFound")
	public String notFound(Model model){
		model.addAttribute("message", "No encontrado");
		return "error";
	}
	@GetMapping("/error/NotYourBox")
	public String notYourBox(Model model){
		model.addAttribute("message", "No es tu caja, amigo");
		return "error";
	}
	@GetMapping("/error/NotYourFile")
	public String notYourFile(Model model){
		model.addAttribute("message", "El archivo que buscas no es tuyo.");
		return "error";
	}
	@GetMapping("/error/emailInUse")
	public String emailInUse(Model model){
		model.addAttribute("message", "El email ya está en uso");
		return "error";
	}

	@GetMapping("/error/invalidFile")
	public String invalidFile(Model model){
		model.addAttribute("message", "Archivo no válido");
		return "error";
	}
}
