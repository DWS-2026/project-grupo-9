package es.codeurjc.web.contoller;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.BoxMapper;
import es.codeurjc.web.dto.BoxDTO;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.BoxService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/boxes")
public class BoxRestController {
	@Autowired
	private BoxMapper mapper;

    @Autowired
	private BoxService boxService;

	@Autowired
	private UserService userService;
	        
	/* hecho
   				get de cajas cerradas de admin (las de /products), de cajas cerradas de admin y custom de un user (abiertas y cerradas) y de todas las cajas (si es admin)
			    get de caja por id
			    delete de caja 

		POR HACER
				cerrar la caja //(que se pueda cerrar la caja, y que al cerrarla ya no se puedan añadir bombones ni nada, y que se pueda hacer el pedido con esa caja cerrada)
				delete de lista de bombones de caja
			    añadir bombón a la caja
			    crear caja (con opción de random)
				randomizar caja (en el post)
		En el post:
		Si es random, se crea con el nombre de "caja random" y ahí también se hace lo de generar la lista aleatoria de bombones y meterla
		Si es custom, se creea con el nombre de "Caja perosnalizada", se crea con la lista de bombones vacía y algo más.
	*/

	@GetMapping("/") 
	public Collection<BoxDTO> getAdminBoxes(HttpServletRequest request) {
        if(request.getUserPrincipal() ==null){ //not registered user sees the boxes made by the admin that are available 
			return mapper.toDTOs(boxService.findByMadeByAdminAndIsAvailable(true, true));
		}

		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(user.isThisRole("ADMIN")){ //admin user sees all the boxes
			return mapper.toDTOs(boxService.findAll());
		}else{ //registered user gets admin boxes and its own (open and closed)
			return mapper.toDTOs(boxService.findOwnedAndAdminBoxes(user));
		}

	}


	
	@GetMapping("/{id}") 
	public ResponseEntity<BoxDTO> getBox(@PathVariable long id, HttpServletRequest request) {
		Box box = boxService.findById(id).orElseThrow();

		if(request.getUserPrincipal() == null){ //not registered
			if(box.getMadeByAdmin() && box.getIsAvailable()){
				return ResponseEntity.ok(mapper.toDTO(box));
			}else{
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if(boxService.hasPermission(user, box, true)){
			return ResponseEntity.ok(mapper.toDTO(box));
		}
		//the user is not the owner of the box and not admin
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		 
	}
    




 



	
	@DeleteMapping("/{id}") 
	public ResponseEntity<Box> deleteBox(@PathVariable Long id, HttpServletRequest request) {
		Box box = boxService.findById(id).orElseThrow();
		if(box.getIsAvailable()){
			if(boxService.hasPermission(userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(), box, true)){
				boxService.delete(box);
				return ResponseEntity.ok(box);
			}
		}
		//if the box isn't available
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  //forbidden pq la caja no está disponible
		
	}



}