package es.codeurjc.web.contoller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

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

import es.codeurjc.web.dto.BoxGetDTO;
import es.codeurjc.web.dto.BoxPostDTO;
import es.codeurjc.web.dto.BoxPostMapper;
import es.codeurjc.web.dto.BoxGetMapper;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.BoxService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/boxes")
public class BoxRestController {
	@Autowired
	private BoxGetMapper boxGetMapper;

	@Autowired
	private BoxPostMapper boxPostMapper;

    @Autowired
	private BoxService boxService;

	@Autowired
	private UserService userService;
	        
	/* hecho
   				get de cajas cerradas de admin (las de /products), de cajas cerradas de admin y custom de un user (abiertas y cerradas) y de todas las cajas (si es admin)
			    get de caja por id
			    delete de caja 
				delete de lista de bombones de caja
			    crear caja (con opción de random)

		POR HACER
				cerrar la caja //(que se pueda cerrar la caja, y que al cerrarla ya no se puedan añadir bombones ni nada, y que se pueda hacer el pedido con esa caja cerrada)
			    añadir bombón a la caja
		En el post:
		Si es random, se crea con el nombre de "caja random" y ahí también se hace lo de generar la lista aleatoria de bombones y meterla
		Si es custom, se creea con el nombre de "Caja perosnalizada", se crea con la lista de bombones vacía y algo más.
	*/

	@GetMapping("/") 
	public Collection<BoxGetDTO> getAdminBoxes(HttpServletRequest request) {
        if(request.getUserPrincipal() ==null){ //not registered user sees the boxes made by the admin that are available 
			return boxGetMapper.toDTOs(boxService.findByMadeByAdminAndIsAvailable(true, true));
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
			}
		}
		//if the box isn't available
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  //forbidden pq la caja no está disponible
		
	}

 	@DeleteMapping("/{id}/chocolates") //vaciar chocolates lista de bombones en caja
 	public ResponseEntity<BoxGetDTO> emptyBox(@PathVariable Long id, HttpServletRequest request) {
 		Box box = boxService.findById(id).orElseThrow();  
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

}