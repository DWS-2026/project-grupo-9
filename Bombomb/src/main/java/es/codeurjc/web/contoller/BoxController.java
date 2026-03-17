package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import java.util.ArrayList;
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
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.OrderService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.web.service.UserService;

import org.springframework.core.io.Resource;


@Controller
public class BoxController {

    @Autowired
	BoxRepository boxes;
	@Autowired
	ChocolateService chocolateService;
	@Autowired
	OrderService orderService;
	@Autowired
	UserService userService;




	@PostConstruct
    public void init() throws SerialException, SQLException, IOException {
		List<Chocolate> chocolates = chocolateService.findAll();
		ClassPathResource resource = new ClassPathResource("static/images/box_heart2.png");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		boxes.save(new Box("Caja 1", 19.50f,
			blob, true, chocolates));


		ClassPathResource resource2 = new ClassPathResource("static/images/box_red2.png");
		byte[] bytes2 = resource2.getInputStream().readAllBytes();
		Blob blob2 = new SerialBlob(bytes2);
		boxes.save(new Box("Caja 2", 18.50f,
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
		model.addAttribute("chocolates", chocolateService.findAll());
		return "customBox";
	}

	@GetMapping("/cart")
	public String cart(Model model, HttpServletRequest request) {

		String userEmail = request.getUserPrincipal().getName();

		Order order = orderService.findByUserEmailAndIsOpen(userEmail,
		 	true).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No se encontró un carrito activo para el usuario"));
		model.addAttribute("order", order);

		int amount = order.getBoxes().size();
		return "cart";
	}

	@PostMapping("/product/{id}/add-to-cart")
    public String addToCart(@PathVariable long id, HttpServletRequest request) {

		String userEmail = request.getUserPrincipal().getName();

		if(orderService.isBoxInCart(userEmail, id) == false) {
        Box box = boxes.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        orderService.addBoxToCart(userEmail, box);
		}

		return "redirect:/products";
    }

	@PostMapping("/order/close-cart")
    public String closeCart(HttpServletRequest request) {


		String userEmail = request.getUserPrincipal().getName();
        orderService.closeTheCart(userEmail);
		orderService.createNewCart(userEmail);
        return "redirect:/success";
    }

	@PostMapping("/delete-from-cart/{id}/box")
	public String deleteBoxFromCart(@PathVariable long id, HttpServletRequest request) {

		String userEmail = request.getUserPrincipal().getName();
		orderService.removeBoxFromCart(userEmail, id);
		
		return "redirect:/cart";
	}
	


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	// al añadirlo al carrito, se crea la caja, con la imagen y la lista de bomobnes y eso
// que se haga como en el createproduct 
// y que luego que añada al order	
	@PostMapping("/custom/{id}/add-to-cart")
    public String addCustomToCart(@PathVariable long id, HttpServletRequest request) {
		String userEmail = request.getUserPrincipal().getName();
        Box box = boxes.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
		box.setName("Caja personalizada");
		box.setPrice(65.00f);
		box.setMadeByAdmin(false);
		box.setIsOpenBox(false);
        orderService.addBoxToCart(userEmail,box);
        return "redirect:/products";
    }

/////coger la caja actual, añadirle a la lista el chocolate con su id
	@PostMapping("/add/{id}") //{id}=chocolate id
	public String addToCustomBox(@PathVariable long id, Model model, HttpServletRequest request) {
		Chocolate chocolate = chocolateService.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
		
		String userEmail = request.getUserPrincipal().getName();	
		Optional<Box> op = boxes.findByIsOpenBoxAndOrdersIsOpenAndOrdersUserEmail( true, true, userEmail);
		Box box;
		
		if (op.isPresent() && op.get().getImage() != null) {
			box =op.get();
		} else {
			box = new Box("", 25.73f, null, false, null);
			box.setIsOpenBox(true);
		}

		List<Chocolate> chocolates = box.getChocolates();
		if (chocolates == null) {
			chocolates = new ArrayList<>();
		}
		int currentSize = chocolates.size();
		if (currentSize >= box.getSize()) {
			//throw new IllegalStateException("La caja ya está llena");
		}else{
			chocolates.add(chocolate);
			box.setChocolates(chocolates);
		}
		boxes.save(box); //boxRepository.save(box);
		orderService.addBoxToCart(userEmail, box);

		model.addAttribute("boxChocolates", chocolates);
		return "redirect:/customBox";
	}
	

//aleatorio
	@PostMapping("/random/{id}")//id?
	public String postMethodName(@PathVariable long id) {//id?
		//que sea aleatorio y eso y que se meta directamente al carrito

		
		return "redirect:/cart";
	}
	
}
