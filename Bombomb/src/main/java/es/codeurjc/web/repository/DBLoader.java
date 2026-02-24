package es.codeurjc.web.repository;

import org.springframework.beans.factory.annotation.Autowired;
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
    private void initDatabase() {
    	
    	userRepository.save(new User("user", passwordEncoder.encode("pass"), "USER"));
		userRepository.save(new User("admin", passwordEncoder.encode("adminpass"), "USER", "ADMIN"));
    }
}