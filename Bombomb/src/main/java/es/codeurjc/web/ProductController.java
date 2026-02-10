package main.java.es.codeurjc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @GetMapping("/products")
	public String products(Model model) {
		return "productsPage";
	}

    @GetMapping("/createproduct")
	public String createProduct(Model model) {
		return "createProduct";
	}

    @GetMapping("/editproduct")
	public String editProduct(Model model) {
		return "editProductPage";
	}

    @GetMapping("/productdetails")
	public String productDetails(Model model) {
		return "productDeatilsPage";
	}
}

