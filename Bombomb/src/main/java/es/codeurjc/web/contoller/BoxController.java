package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import java.util.List;

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

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.repository.OrderRepository;
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.OrderService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

@Controller
public class BoxController {

	@Autowired
	BoxRepository boxes;
	@Autowired
	ChocolateService chocolateService;
	@Autowired
	OrderService orderService;


	@PostConstruct
    public void init() throws SerialException, SQLException, IOException {
		List<Chocolate> chocolates = chocolateService.findAll();
		ClassPathResource resource = new ClassPathResource("static/images/box_heart2.png");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		boxes.save(new Box("Caja 1", "19.50€", "Caja con 12 bombones violeta", 
			blob, true, chocolates));


		ClassPathResource resource2 = new ClassPathResource("static/images/box_red2.png");
		byte[] bytes2 = resource2.getInputStream().readAllBytes();
		Blob blob2 = new SerialBlob(bytes2);
		boxes.save(new Box("Caja 2", "18.50€", "Caja con 12 bombones limón", 
			blob2, true, chocolates));

    }

	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("chocolates", chocolateService.findAll());
		model.addAttribute("boxes", boxes.findAll());
		return "productsPage";
	}

	@PostMapping("/createproduct")
	public String newProduct(Model model, Box box, MultipartFile imageFile) throws IOException {
		if (!imageFile.isEmpty()) {
			try {
				box.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		boxes.save(box);
		model.addAttribute("boxes", boxes.findAll());
		return "redirect:/products";
	}

	@GetMapping("/product/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
		Optional<Box> op = boxes.findById(id);
		if (op.isPresent() && op.get().getImage() != null) {
			Blob image = op.get().getImage();
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
		Optional <Box> box = boxes.findById(id);
		if(box.isPresent()){
			model.addAttribute("product", box);
			return "productDetailsPage";
		}else{
			model.addAttribute("message", "Producto no encontrado");
			return "error";
		}
	}

	@GetMapping("/customBox")
	public String customBox(Model model) {
		return "customBox";
	}

	@GetMapping("/cart")
	public String cart(Model model, HttpServletRequest request) {

		Order order = orderService.findByUserEmailAndIsOpen(request.getUserPrincipal().getName(),
		 	true).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No se encontró un carrito activo para el usuario"));
		model.addAttribute("order", order);
		return "cart";
	}

	//////////////////////////////////////////////////////////////////////////
	@PostMapping("/product/{id}/add-to-cart")
    public String addToCart(@PathVariable long id) {
        Box box = boxes.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        orderService.addBoxToCart(box);
        return "redirect:/products";
    }


}
