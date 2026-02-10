package es.codeurjc.web;
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
}
