package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.BoxService;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.web.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class BoxController {

    @Autowired
	BoxService boxService;
	@Autowired
	ChocolateService chocolateService;
	@Autowired
	OrderService orderService;
	@Autowired
	UserService userService;
	@Autowired
	ImageService imageService;
	

	@GetMapping("/products")
	public String products(Model model, HttpServletRequest request) {
		if(request.isUserInRole("ADMIN")){
			model.addAttribute("admin", true);
		}
		model.addAttribute("chocolates", chocolateService.findByIsAvailable(true));
		model.addAttribute("boxes", boxService.findByMadeByAdminAndIsOpenBoxAndIsAvailable(true, false, true));
		return "productsPage";
	}

	@GetMapping("/product/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
		Optional<Box> op = boxService.findByIdAndIsAvailable(id, true);
		if (op.isPresent() && op.get().getImage() != null) {
			return imageService.getImage(op.get().getImage().getImage());
		} else {
        	return imageService.getNotFoundImage();
		}
	}

	@GetMapping("/product/{id}/details")
	public String productDetails(Model model, @PathVariable long id) {
		Optional <Box> op = boxService.findByIdAndIsAvailable(id, true);

		if(op.isPresent()){
			Box box = op.get();
			if(box.getMadeByAdmin()){ //if the box wasn't made by an admin, it doesn't show
				model.addAttribute("box", box);
				model.addAttribute("boxChocolates", box.getChocolates());
				return "productDetailsPage";
			}else{
				return "redirect:/error/notFound";
			}
		}else{
			return "redirect:/error/notFound";
		}
	}

	@GetMapping("/customBox")
	public String customBox(Model model, HttpServletRequest request) {
		model.addAttribute("chocolates", chocolateService.findByIsAvailable(true));
		String userEmail = request.getUserPrincipal().getName();
		User user = userService.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		if(userService.isAdminRole(user)){  
			model.addAttribute("admin", true);
		}
		Optional<Box> op = boxService.findBoxByStatusAndUserEmail(true, true, userEmail); 
		if (op.isPresent()) {
			model.addAttribute("box", op.get());
			model.addAttribute("boxChocolates", op.get().getChocolates());
		} 
		return "customBox";
	}

	@GetMapping("/cart")
	public String cart(Model model, HttpServletRequest request) {

		String userEmail = request.getUserPrincipal().getName();

		Order order = orderService.findByUserEmailAndIsOpen(userEmail,
		 	true).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No se encontró un carrito activo para el usuario"));
		order.updatePrice();
		model.addAttribute("order", order);
		return "cart";
	}

	@PostMapping("/product/{id}/add-to-cart")
    public String addToCart(@PathVariable long id, HttpServletRequest request) {
		String userEmail = request.getUserPrincipal().getName();

        Optional<Box> op = boxService.findByIdAndIsAvailableAndMadeByAdmin(id, true, true);
		if(!op.isPresent()){
			return "redirect:/error/notFound";
		}
		Box box = op.get();
        orderService.addBoxToCart(userEmail, box);
		
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

	@PostMapping("/custom/{id}/add-to-cart") 
    public String addCustomToCart(@PathVariable long id, HttpServletRequest request, @RequestParam String name) throws IOException, SQLException {	
		String userEmail = request.getUserPrincipal().getName();
        Optional<Box> op = boxService.findByIdAndIsAvailable(id, true);
		if(!op.isPresent()){
			return "redirect:/error/notFound";
		}
		if(!orderService.isBoxInCart( userEmail,id)){
			return "redirect:/error/NotYourBox";
		}
		Box box = op.get();
		box.setName(name);
		boxService.addCustomToCart(box, userEmail);
		boxService.save(box);
		return "redirect:/cart";
    }
	


	@PostMapping("/addChocolate/{id}") //{id}=chocolate id
	public String addToCustomBox(@PathVariable long id, Model model, HttpServletRequest request,RedirectAttributes redirectAttributes) {
		Chocolate chocolate = chocolateService.findByIdAndIsAvailable(id, true).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
		String userEmail = request.getUserPrincipal().getName();	
		
		Optional<Box> op = boxService.findBoxByStatusAndUserEmail(true, true, userEmail); 
		Box box;
		if (op.isPresent()) {
			box =op.get();	
		} else {
			box = boxService.createBox("Caja personalizada", 0.0f, null, false, new ArrayList<>(), userEmail);
		}
		if (boxService.isBoxFull(box)) {//if box has 9 chocolates(max size)
			model.addAttribute("message", "La caja ya está llena");
			return "error";
		}else{
			boxService.addChocolateToBox(box, chocolate);
		}
		boxService.save(box);
		redirectAttributes.addFlashAttribute("boxChocolates", box.getChocolates());
		return "redirect:/customBox";
	}
	
	@PostMapping("/emptyCustom")//empty the box
	public String emptyCustomBox(Model model, HttpServletRequest request) {
		String userEmail = request.getUserPrincipal().getName();
		Optional<Box> op = boxService.findBoxByStatusAndUserEmail(true, true, userEmail); 
		Box box;
		if (op.isPresent()) {
			box =op.get();
			box.getChocolates().clear();
			boxService.save(box);
		} 
		return "redirect:/customBox";
	}
	
	@PostMapping("/randomize")//make chocolate list random
	public String randomCustom(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String userEmail = request.getUserPrincipal().getName();	
		Optional<Box> op = boxService.findBoxByStatusAndUserEmail(true, true, userEmail); 
		Box box;
		if (op.isPresent()) {
			box =op.get();
		} else {
			box = boxService.createBox("Caja aleatoria", 0.0f, null, false, new ArrayList<>(), userEmail);
		}
		List<Chocolate> chocolates = box.getChocolates();
		chocolates.clear();
		//if the box is empty, fill it with random chocolates, if not,emty it and fill it with random chocolates
		int totalSize = chocolateService.findByIsAvailable(true).size();
		int boxSize = box.getSize();

		for(int i=0; i<boxSize; i++){
			int randomIndex = (int) (Math.random() * totalSize);
			chocolates.add(chocolateService.findByIsAvailable(true).get(randomIndex));
		}
		boxService.save(box); 

		redirectAttributes.addFlashAttribute("boxChocolates", chocolates);
		return "redirect:/customBox";
	}

	@PostMapping("/adminAddBox/{id}") 
	public String adminAddBox(@PathVariable long id, @RequestParam MultipartFile imageFile, HttpServletRequest request, @RequestParam String name) throws IOException {
        Optional<Box> op = boxService.findByIdAndIsAvailable(id, true);
		if(!op.isPresent()){
			return "redirect:/error/notFound";
		}
		Box box = op.get();
		if (!imageFile.isEmpty()) {
			try {
				box.setImage(new Image(new SerialBlob(imageFile.getBytes())));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}	
		box.setName(name);
		box.setMadeByAdmin(true);
		box.setIsOpenBox(false);
		box.setPrice(19.0f);
		boxService.save(box);
		
		return "redirect:/products";
	}
	
	@PostMapping("/box/{id}/delete")
	public String boxDelete(@PathVariable long id, HttpServletRequest request){
		Optional<Box> op = boxService.findByIdAndIsAvailable(id, true);
		if(!op.isPresent()){
			return "redirect:/error/notFound";
		}else{
			Box box= op.get();
			boxService.delete(box);
			return "redirect:/products";
		}

	}

}
