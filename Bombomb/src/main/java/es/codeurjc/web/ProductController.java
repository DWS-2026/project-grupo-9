package es.codeurjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.annotation.PostConstruct;

@Controller
public class ProductController {

	@Autowired
	ProductRepository products;

	@PostConstruct
	public void init(){
		products.save(new Product("Violeta", "0.60€",
		 "Bombón en forma de flor relleno de moras y brillo metalizado",
		  "12", "/images/chocolate_flower.jpeg", "Bombón"));
		products.save(new Product("a", "aa", "aaa", "aaaa", "aaaaa", "aaaaa"));
	}

    @GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("name", "Violeta");
		model.addAttribute("price", "0.60€");
		model.addAttribute("image", "images/chocolate_flower.jpeg");
		return "productsPage";
	}


    @GetMapping("/createproduct")
	public String createProduct(Model model) {
		return "createProduct";
	}

    @GetMapping("/editproduct")
	public String editProduct(Model model) {
		model.addAttribute("name", "Violeta");
		model.addAttribute("price", "0.60€");
		model.addAttribute("description", "Bombón en forma de flor relleno de moras y brillo metalizado");
		model.addAttribute("stock", "12");
		model.addAttribute("image", "images/chocolate_flower.jpeg");
		return "editProductPage";
	}

    @GetMapping("/product/{id}/details")
	public String productDetails(Model model, @PathVariable long id) {
		Product product = products.getById(id);
		model.addAttribute("product", product);
		return "productDetailsPage";
	}

	@GetMapping("/customBox")
	public String customBox(Model model) {
		return "customBox";
	}

	@GetMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("image", "images/chocolate_pink.jpeg");
		model.addAttribute("image2","images/chocolate_lemon.jpeg");

		return "cart";
	}
}

