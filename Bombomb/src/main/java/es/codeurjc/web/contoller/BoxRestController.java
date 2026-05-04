package es.codeurjc.web.contoller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.BoxGetDTO;
import es.codeurjc.web.dto.BoxPostDTO;
import es.codeurjc.web.dto.BoxPostMapper;
import es.codeurjc.web.dto.ImageDTO;
import es.codeurjc.web.dto.ImageMapper;
import es.codeurjc.web.dto.IsOpenRequest;
import es.codeurjc.web.dto.BoxGetMapper;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.BoxService;
import es.codeurjc.web.service.ChocolateService;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;


@RestController
@RequestMapping("/api/v1/boxes")
public class BoxRestController {
	@Autowired
	private BoxGetMapper boxGetMapper;

	@Autowired
	private BoxPostMapper boxPostMapper;

	@Autowired
	private ImageMapper imageMapper;

    @Autowired
	private BoxService boxService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;
	
	@Autowired
	private ChocolateService chocolateService;

	@GetMapping("/") 
	public Collection<BoxGetDTO> getAdminBoxes(HttpServletRequest request) {
        if(request.getUserPrincipal() ==null){ //not registered user sees the boxes made by the admin that are available 
			return boxGetMapper.toDTOs(boxService.findByMadeByAdminAndIsAvailableAndIsOpenBox(true, true, true));
		}

		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(user.isThisRole("ADMIN")){ //admin user sees all the boxes
			return boxGetMapper.toDTOs(boxService.findAll());
		}else{ //registered user gets admin boxes and its own (open and closed)
			return boxGetMapper.toDTOs(boxService.findOwnedAndAdminBoxes(user));
		}

	}

	
	@GetMapping("/{id}") 
	public ResponseEntity<BoxGetDTO> getBox(@PathVariable long id, HttpServletRequest request) {
		Box box = boxService.findById(id).orElseThrow();

		if(request.getUserPrincipal() == null){ //not registered
			if(box.getMadeByAdmin() && box.getIsAvailable()){
				return ResponseEntity.ok(boxGetMapper.toDTO(box));
			}else{
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(boxService.hasPermission(user, box, true)){
			return ResponseEntity.ok(boxGetMapper.toDTO(box));
		}
		//the user is not the owner of the box and not admin
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		 
	}
    
	
	@DeleteMapping("/{id}") 
	public ResponseEntity<BoxGetDTO> deleteBox(@PathVariable Long id, HttpServletRequest request) {
		Box box = boxService.findById(id).orElseThrow();
		if(box.getIsAvailable()){
			if(boxService.hasPermission(userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(), box, false)){
				boxService.delete(box);
				return ResponseEntity.ok(boxGetMapper.toDTO(box));
			}else if(box.getMadeByAdmin() && userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow().isThisRole("ADMIN")){
				boxService.delete(box);
				return ResponseEntity.ok(boxGetMapper.toDTO(box));
			}
	}
		//if the box isn't available
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  
		
	}


 	@DeleteMapping("/chocolates") //vaciar chocolates lista de bombones en caja
 	public ResponseEntity<BoxGetDTO> emptyBox(HttpServletRequest request) { 
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
 		Box box = boxService.findBoxByStatusAndUserEmail(true,true, user.getEmail()).orElseThrow(); 
		if(boxService.hasPermission(userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(), box, false)){
		//only the owner can empty the box
			box.getChocolates().clear();
			boxService.save(box);
		}
 		return ResponseEntity.ok(boxGetMapper.toDTO(box));

 	}


	@PostMapping("/")
	public ResponseEntity<BoxGetDTO> createBox(@RequestBody BoxPostDTO boxDTO, @RequestParam(required = false) Boolean isRandom, HttpServletRequest request) throws IOException {
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();

		//if the user already has an open box, it can't create another one
		Optional<Box> existingBox = boxService.findBoxByStatusAndUserEmail(true, true, user.getEmail());
		if (existingBox.isPresent()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Box box = boxPostMapper.toDomain(boxDTO);
		boxService.createApiBox(box, user);
		if(isRandom != null && isRandom != false){ 
			boxService.randomizeBox(box);
		}
		BoxGetDTO responseDTO = boxGetMapper.toDTO(box);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri(); 

		return ResponseEntity.created(location).body(responseDTO);
	}


	@PutMapping("/chocolates/{chocolateId}") //añadir bombon
	public ResponseEntity<BoxGetDTO> addChocolateToBox(@PathVariable long boxId, @PathVariable long chocolateId, HttpServletRequest request) { 
		String userEmail = request.getUserPrincipal().getName();	
		
		Optional<Box> op = boxService.findBoxByStatusAndUserEmail(true, true, userEmail);  
		if(op.isPresent()){
			Box box = op.get();
			if(boxService.hasPermission(userService.findByEmail(userEmail).get(), box, false)){
				if(!boxService.isBoxFull(box)){ //box is not full
					Optional<Chocolate> chocolate = chocolateService.findById(chocolateId); 
					if(chocolate.isPresent()) {
						boxService.addChocolateToBox(box, chocolate.get()); 
						boxService.save(box);
						return ResponseEntity.ok(boxGetMapper.toDTO(box));
					}
				}else{ //box is full
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				}
			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}


	@PatchMapping("/isOpenBox") 
	public ResponseEntity<BoxGetDTO> closeBox(HttpServletRequest request, @RequestBody IsOpenRequest isOpenRequest) throws IOException, SQLException {
		User user =  userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		Optional<Box> op= boxService.findBoxByStatusAndUserEmail(true, true, user.getEmail());
		if(op.isPresent()){
			Box box = op.get();
		
			if(boxService.hasPermission(user, box, false)){  
				
				if(isOpenRequest.getIsOpen()==false){
					if(userService.isAdminRole(user)){
						boxService.closeBox(null, true, 19.0f, box);
					}else{
						boxService.closeBox(null, false, 19.99f, box);
						boxService.addCustomToCart(box, user.getEmail());
					}
					return ResponseEntity.ok(boxGetMapper.toDTO(box));
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();	
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();	
	}

	@PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
	public ResponseEntity<ImageDTO> createBoxImage(@PathVariable long id,
			@RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException, SerialException, SQLException {
		if (imageFile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		Box box = boxService.findById(id).orElseThrow();
		User user =  userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(boxService.hasPermission(user, box, false)){
			Image image = boxService.findByIdAndIsAvailable(id, true).orElseThrow().getImage();
			if (image.getBlobImage() == null) {//Only add image if it does not have one
				imageService.replaceImage(image.getId(), imageFile);
			}
			URI location = fromCurrentContextPath()
				.path("/images/{imageId}/media")
				.buildAndExpand(image.getId())
				.toUri();
			return ResponseEntity.created(location).body(imageMapper.toDTO(image));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

	}
}