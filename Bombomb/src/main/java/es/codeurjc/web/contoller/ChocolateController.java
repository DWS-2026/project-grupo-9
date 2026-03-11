package es.codeurjc.web.contoller;

import java.io.IOException;
import java.sql.SQLException;

import java.sql.Blob;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.service.ChocolateService;

@Controller
public class ChocolateController {
    @Autowired
	ChocolateService chocolateService;
        
    @GetMapping("/createproduct")
	public String createProduct(Model model) {
		return "createChocolate";
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

    @PostMapping("/delete/{id}/chocolate")
	public String deleteChocolate(@PathVariable long id) {
		chocolateService.deleteById(id);
		return "redirect:/products";
	}
}
