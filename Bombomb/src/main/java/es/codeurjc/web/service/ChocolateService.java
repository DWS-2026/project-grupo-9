package es.codeurjc.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.repository.ChocolateRepository;

@Service
public class ChocolateService {

    @Autowired
    private ChocolateRepository chocolateRepository;

    @Autowired
    private BoxService boxService;

    public void save(Chocolate chocolate, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            try {
                chocolate.setImage(new Image(new SerialBlob(image.getBytes())));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
        }
        chocolateRepository.save(chocolate);
    }

    public List<Chocolate> findAll() {
        return chocolateRepository.findAll();
    }

    public Optional<Chocolate> findById(long id) {
        return chocolateRepository.findById(id);
    }

    public void deleteById(long id) {
        Optional<Chocolate> op = chocolateRepository.findById(id);
        if (op.isPresent()) {
            Chocolate chocolate = op.get();
            chocolate.setIsAvailable(false);
            chocolateRepository.save(chocolate);

            List<Box> boxes = boxService.findByChocolatesId(id);
            for (Box box : boxes) {
                boxService.delete(box);

            }
        }
    }

    public List <Chocolate> findByIsAvailable(Boolean isAvailable){
        return chocolateRepository.findByIsAvailable(isAvailable);
    }

    public Optional <Chocolate> findByIdAndIsAvailable(long id, Boolean isAvailable){
        return chocolateRepository.findByIdAndIsAvailable(id, isAvailable);
    }

}
