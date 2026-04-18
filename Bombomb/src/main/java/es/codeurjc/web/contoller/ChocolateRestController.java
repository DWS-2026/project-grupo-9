package es.codeurjc.web.contoller;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.dto.ChocolateDTO;
import es.codeurjc.web.dto.ChocolateMapper;
import es.codeurjc.web.service.ChocolateService;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/chocolates")

public class ChocolateRestController {
    @Autowired
	private ChocolateService chocolateService;

	@Autowired
	private ChocolateMapper mapper;

	@GetMapping("/")
	public Collection<ChocolateDTO> getChocolates() {
		return mapper.toDTOs(chocolateService.findByIsAvailable(true));
	}

	@GetMapping("/{id}")
	public ChocolateDTO getChocolate(@PathVariable long id) {
		return mapper.toDTO(chocolateService.findByIdAndIsAvailable(id, true).orElseThrow());
	}
	
	@PostMapping("/")
	public ResponseEntity<ChocolateDTO> createChocolate(@RequestBody ChocolateDTO chocolateDTO) throws IOException {

		Chocolate chocolate = mapper.toDomain(chocolateDTO);
		chocolateService.save(chocolate, null);
		ChocolateDTO responseDTO = mapper.toDTO(chocolate);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

		return ResponseEntity.created(location).body(responseDTO);
	}

	@DeleteMapping("/{id}")
	public ChocolateDTO deleteChocolate(@PathVariable long id) {
		chocolateService.deleteById(id);
		return mapper.toDTO(chocolateService.findById(id).orElseThrow());
		//return mapper.toDTO(chocolateService.deleteById(id));//My delete method does not return the deleted chocolate
	}
}