package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Product;
import es.codeurjc.web.repository.ProductRepository;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.OrderService;

import org.springframework.core.io.Resource;

@Controller
public class ProductController {

	@Autowired
	ProductRepository products;
	@Autowired
	ChocolateService chocolateService;
	@Autowired
	OrderService orderService;

	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("chocolates", chocolateService.findAll());
		return "productsPage";
	}

	@GetMapping("/createproduct")
	public String createProduct(Model model) {
		return "createChocolate";
	}

	@PostMapping("/createproduct")
	public String newProduct(Model model, Product product, MultipartFile imageFile) throws IOException {
		if (!imageFile.isEmpty()) {
			try {
				product.setImageFile(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		products.save(product);
		model.addAttribute("products", products.findAll());
		return "redirect:/products";
	}

	@PostMapping("/create/chocolate")
	public String newChocolate(Model model, Chocolate chocolate, MultipartFile imageFile) throws IOException {
		chocolateService.save(chocolate, imageFile);
		model.addAttribute("products", chocolateService.findAll());
		return "redirect:/products";
	}

	@GetMapping("/chocolate/{id}/image")
	public ResponseEntity<Object> downloadChocolateImage(@PathVariable long id) throws SQLException {
		Optional<Chocolate> op = chocolateService.findById(id);
		if (op.isPresent() && op.get().getImage() != null) {
			Blob image = op.get().getImage();
			Resource imageFile = new InputStreamResource(image.getBinaryStream());
			MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
			return ResponseEntity.ok().contentType(mediaType).body(imageFile);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/product/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
		Optional<Product> op = products.findById(id);
		if (op.isPresent() && op.get().getImageFile() != null) {
			Blob image = op.get().getImageFile();
			Resource imageFile = new InputStreamResource(image.getBinaryStream());
			MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
			return ResponseEntity.ok().contentType(mediaType).body(imageFile);
		} else {
			return ResponseEntity.notFound().build();
		}
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
		model.addAttribute("image2", "images/chocolate_lemon.jpeg");

		return "cart";
	}

	@PostMapping("/delete/{id}/chocolate")
	public String deleteChocolate(@PathVariable long id) {
		chocolateService.deleteById(id);
		return "redirect:/products";
	}

	//////////////////////////////////////////////////////////////////////////
	@PostMapping("/product/{id}/add-to-cart")
    public String addToCart(@PathVariable long id) {
        Product product = products.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        orderService.addProductToCart(product);
        return "redirect:/cart";
    }
}
