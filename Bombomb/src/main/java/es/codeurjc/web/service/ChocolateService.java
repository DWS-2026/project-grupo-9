package es.codeurjc.web.service;

import java.io.IOException;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.repository.ChocolateRepository;
import jakarta.annotation.PostConstruct;

@Service
public class ChocolateService {

    @Autowired
    private ChocolateRepository chocolateRepository;

    @PostConstruct
	public void init() throws Exception {
		ClassPathResource resource = new ClassPathResource("static/images/chocolate_flower.jpeg");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		chocolateRepository.save(new Chocolate("Violeta", blob));
	}

    public void save(Chocolate chocolate, MultipartFile image) throws IOException{
        if (!image.isEmpty()) {
			try {
				chocolate.setImage(new SerialBlob(image.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
        chocolateRepository.save(chocolate);
    }

    public List<Chocolate> findAll(){
        return chocolateRepository.findAll();
    }

    public Optional<Chocolate> findById(long id){
        return chocolateRepository.findById(id);
    }

    public void deleteById(long id){
        chocolateRepository.deleteById(id);
    }


}
