package es.codeurjc.web.contoller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.dto.ChocolateDTO;
import es.codeurjc.web.dto.ChocolateMapper;
import es.codeurjc.web.dto.ImageDTO;
import es.codeurjc.web.dto.ImageMapper;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.ImageService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping("/api/v1/chocolates")

public class ChocolateRestController {
	@Autowired
	private ChocolateService chocolateService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ChocolateMapper chocolateMapper;

	@Autowired
	private ImageMapper imageMapper;

	@GetMapping("/")
	public Collection<ChocolateDTO> getChocolates() {
		return chocolateMapper.toDTOs(chocolateService.findByIsAvailable(true));
	}

	@GetMapping("/{id}")
	public ChocolateDTO getChocolate(@PathVariable long id) {
		return chocolateMapper.toDTO(chocolateService.findByIdAndIsAvailable(id, true).orElseThrow());
	}

	@PostMapping("/")
	public ResponseEntity<ChocolateDTO> createChocolate(@RequestBody ChocolateDTO chocolateDTO) throws IOException {

		Chocolate chocolate = chocolateMapper.toDomain(chocolateDTO);
		chocolateService.save(chocolate, null);
		ChocolateDTO responseDTO = chocolateMapper.toDTO(chocolate);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

		return ResponseEntity.created(location).body(responseDTO);
	}

	@DeleteMapping("/{id}")
	public ChocolateDTO deleteChocolate(@PathVariable long id) {
		chocolateService.deleteById(id);
		return chocolateMapper.toDTO(chocolateService.findById(id).orElseThrow());
	}

	@PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
	public ResponseEntity<ImageDTO> createChocolateImage(@PathVariable long id,
			@RequestParam MultipartFile imageFile) throws IOException, SerialException, SQLException {
		if (imageFile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		Image image = chocolateService.findByIdAndIsAvailable(id, true).orElseThrow().getImage();
		if (image.getBlobImage() == null) {//Only add image if it does not have one
			imageService.replaceImage(image.getId(), imageFile);
		}
		URI location = fromCurrentContextPath()
				.path("/images/{imageId}/media")
				.buildAndExpand(image.getId())
				.toUri();
		return ResponseEntity.created(location).body(imageMapper.toDTO(image));

	}
}