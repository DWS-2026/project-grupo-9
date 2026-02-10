package es.codeurjc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @GetMapping("/products")
	public String products(Model model) {
		return "productsPage_template";
	}

    @GetMapping("/createproduct")
	public String createProduct(Model model) {
		return "createProduct_template";
	}

    @GetMapping("/editproduct")
	public String editProduct(Model model) {
		return "editProductPage_template";
	}

    @GetMapping("/productdetails")
	public String productDetails(Model model) {
		return "productDeatilsPage_template";
	}
}

