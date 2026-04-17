package es.codeurjc.web;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.repository.ChocolateRepository;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.ChocolateService;
import jakarta.annotation.PostConstruct;

@Component
public class DBLoader {

	@Autowired
	private ChocolateRepository chocolateRepository;
	@Autowired
	private ChocolateService chocolateService;
	@Autowired
	private BoxRepository boxes;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	private void initDatabase() throws IOException, SerialException, SQLException {
		ClassPathResource resource = new ClassPathResource("static/images/Chocolates/chocolate_flower.jpeg");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		userRepository.save(new User("María de la O", "Sánchez Sánchez",
				"600808080", "mariasanchezsanchez@hotmail.com", blob,
				passwordEncoder.encode("pass"), "a","USER"));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		userRepository.save(new User("Administrador", "Adminis Trado",
				"666666666", "admin@gmail.com", blob,
				passwordEncoder.encode("admin"), "buenas","USER", "ADMIN"));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		userRepository.save(new User("Usu", "Ario",
				"000000000", "user@gmail.com", blob,
				passwordEncoder.encode("user"),"holaa" ,"USER"));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_flower.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Violeta", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_black.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Espiral de Medianoche", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_boat.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Barca de Avellana", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_cake.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Triángulo de las delicias", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_cube.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Caramelo Imperial", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_green.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Bosque de cacao", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_lemon.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Delicia de Cítrico", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_orange.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Puesta de Sol", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_pink_heart.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Corazón Silvestre", new Image(blob)));

		resource = new ClassPathResource("static/images/Chocolates/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Mármol de frambuesa", new Image(blob)));

		List <Chocolate> chocolates;
		int box = 1;
		Optional <Chocolate> chocolate = chocolateService.findById(box);
		while(chocolate.isPresent()){
			chocolates = new ArrayList<Chocolate>();
			if(chocolate.isPresent()){
				for(int i = 0; i < 9; i++){
					chocolates.add(chocolate.get());
				}
			}
			if(box%2 == 0){
				resource = new ClassPathResource("static/images/Boxes/box_heart2.png");
			}else{
				if(box%3 == 0){
					resource = new ClassPathResource("static/images/Boxes/boxblackCustomized2.png");
				}else{
					resource = new ClassPathResource("static/images/Boxes/box_red2.png");
				}
			}
			
			bytes = resource.getInputStream().readAllBytes();
			blob = new SerialBlob(bytes);
			boxes.save(new Box("Caja " + box, 18.50f,
			blob, true, chocolates));
			box++;
			chocolate = chocolateService.findById(box);
		}
	
	}
}