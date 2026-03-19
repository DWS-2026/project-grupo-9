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
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.repository.ChocolateRepository;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.ChocolateService;
import jakarta.annotation.PostConstruct;

/*FALTA POR HACER EL 
1.SECURITYCONFIGURATION 
2.KEYSTORE.JKS 
3.WEBCONTROLLER*/
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
		ClassPathResource resource = new ClassPathResource("static/images/chocolate_flower.jpeg");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		userRepository.save(new User("María de la O", "Sánchez Sánchez",
				"600808080", "mariasanchezsanchez@hotmail.com", blob,
				passwordEncoder.encode("pass"), "USER"));

		resource = new ClassPathResource("static/images/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		userRepository.save(new User("Administrador", "Adminis Trado",
				"666 666 666", "admin@gmail.com", blob,
				passwordEncoder.encode("admin"), "USER", "ADMIN"));

		resource = new ClassPathResource("static/images/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		userRepository.save(new User("Usu", "Ario",
				"000 000 000", "user@gmail.com", blob,
				passwordEncoder.encode("user"), "USER"));

		resource = new ClassPathResource("static/images/chocolate_flower.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Violeta", blob));

		resource = new ClassPathResource("static/images/chocolate_black.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Espiral de Medianoche", blob));

		resource = new ClassPathResource("static/images/chocolate_boat.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Barca de Avellana", blob));

		resource = new ClassPathResource("static/images/chocolate_cake.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Triángulo de las delicias", blob));

		resource = new ClassPathResource("static/images/chocolate_cube.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Caramelo Imperial", blob));

		resource = new ClassPathResource("static/images/chocolate_green.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Bosque de cacao", blob));

		resource = new ClassPathResource("static/images/chocolate_lemon.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Delicia de Cítrico", blob));

		resource = new ClassPathResource("static/images/chocolate_orange.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Puesta de Sol", blob));

		resource = new ClassPathResource("static/images/chocolate_pink_heart.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Corazón Silvestre", blob));

		resource = new ClassPathResource("static/images/chocolate_pink.jpeg");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Mármol de frambuesa", blob));

		Optional <Chocolate> chocolate = chocolateService.findById(1);
		List <Chocolate> chocolates = new ArrayList<Chocolate>();
		if(chocolate.isPresent()){
			for(int i = 0; i < 9; i++){
				chocolates.add(chocolate.get());
			}
		}
		resource = new ClassPathResource("static/images/box_heart2.png");
		bytes = resource.getInputStream().readAllBytes();
		blob = new SerialBlob(bytes);
		boxes.save(new Box("Caja 1", 19.50f,
			blob, true, chocolates));

		chocolate = chocolateService.findById(2);
		chocolates = new ArrayList<Chocolate>();
		if(chocolate.isPresent()){
			for(int i = 0; i < 9; i++){
				chocolates.add(chocolate.get());
			}
		}
		ClassPathResource resource2 = new ClassPathResource("static/images/box_red2.png");
		byte[] bytes2 = resource2.getInputStream().readAllBytes();
		Blob blob2 = new SerialBlob(bytes2);
		boxes.save(new Box("Caja 2", 18.50f,
			blob2, true, chocolates));
	}
}