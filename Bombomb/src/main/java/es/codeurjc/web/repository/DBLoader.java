package es.codeurjc.web.repository;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.web.model.User;
import jakarta.annotation.PostConstruct;

/*FALTA POR HACER EL 
1.SECURITYCONFIGURATION 
2.KEYSTORE.JKS 
3.WEBCONTROLLER*/
@Component
public class DBLoader {

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
				"666 666 666", "adminstrador@gmail.com", blob, 
				passwordEncoder.encode("adminpass"), "USER", "ADMIN"));
	}
}