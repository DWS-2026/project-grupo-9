package es.codeurjc.web.contoller;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.BoxDTO;
import es.codeurjc.web.dto.BoxMapper;
import es.codeurjc.web.service.BoxService;

@RestController
@RequestMapping("/api/v1/boxes")
public class BoxRestController {
    @Autowired
	private BoxService boxService;

	@Autowired
	private BoxMapper mapper;

	@GetMapping("/")
	public Collection<BoxDTO> getBoxes(@RequestParam(required = false) Boolean madeByAdmin) {
        if (madeByAdmin == null) {
            //elegir si devolver todas o las cerradas
            return mapper.toDTOs(boxService.findByIsAvailable(true));
        }
		return mapper.toDTOs(boxService.findByMadeByAdminAndIsAvailable(madeByAdmin, true));
	}
    
}
