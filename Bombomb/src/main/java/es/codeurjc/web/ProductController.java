package es.codeurjc.web;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;

@Controller
public class ProductController {

	@Autowired
	ProductRepository products;
	@Autowired
	ChocolateRepository chocolates;

	@PostConstruct
	public void init() throws Exception {
		ClassPathResource resource = new ClassPathResource("static/images/chocolate_flower.jpeg");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		chocolates.save(new Chocolate("Violeta", blob));
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
		if (!imageFile.isEmpty()) {
			try {
				chocolate.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		chocolates.save(chocolate);
		model.addAttribute("products", chocolates.findAll());
		return "redirect:/products";
	}

	@GetMapping("/chocolate/{id}/image")
	public ResponseEntity<Object> downloadChocolateImage(@PathVariable long id) throws SQLException {
		Optional<Chocolate> op = chocolates.findById(id);
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
	public String deleteChocolate(@PathVariable long id){
		chocolates.deleteById(id);
		return "redirect:/products";
	}
}
