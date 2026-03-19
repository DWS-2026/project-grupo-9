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
import es.codeurjc.web.model.Order;
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.repository.ChocolateRepository;

@Service
public class ChocolateService {

    @Autowired
    private ChocolateRepository chocolateRepository;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private OrderService orderService;

    public void save(Chocolate chocolate, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            try {
                chocolate.setImage(new SerialBlob(image.getBytes()));
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
             /*boxService.delete(box)*/

            List<Box> boxes = boxRepository.findByChocolatesId(id);
            for (Box box : boxes) {
                box.setIsAvailable(false);
                List <Order> orders = orderService.findByBoxesAndIsOpen(box, true);
                for(Order order : orders){
                    order.removeBox(box);
                }
                boxRepository.save(box);
               
            }
        }
    }

    public List <Chocolate> findByIsAvailable(Boolean isAvailable){
        return chocolateRepository.findByIsAvailable(isAvailable);
    }

}
