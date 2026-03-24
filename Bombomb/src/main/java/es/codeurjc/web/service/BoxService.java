package es.codeurjc.web.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.repository.BoxRepository;

@Service
public class BoxService {
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private OrderService orderService;



    
    public List<Box> findAll() {
        return boxRepository.findAll();
    }
    public Box findById(Long id) {
        return boxRepository.findById(id).orElse(null);
    }
    public Box save(Box box) {
        return boxRepository.save(box);
    }
    public List<Box> findByMadeByAdminAndIsOpenBoxAndIsAvailable(Boolean madeByAdmin, Boolean isOpenBox, Boolean isAvailable) {
        return boxRepository.findByMadeByAdminAndIsOpenBoxAndIsAvailable(madeByAdmin, isOpenBox, isAvailable);
    }
    public void delete(Box box) {
        box.setIsAvailable(false);
                List <Order> orders = orderService.findByBoxesAndIsOpen(box, true);
                for(Order order : orders){
                    order.removeBox(box);
                }
                boxRepository.save(box);
    }
    public List<Box> findByChocolatesId(long id) {
        return boxRepository.findByChocolatesId(id);
    }
    public Optional<Box> findBoxByStatusAndUserEmail(Boolean isOpenBox, Boolean isOpen, String email) {
        return boxRepository.findBoxByStatusAndUserEmail(isOpenBox, isOpen, email);
    }
    public Optional<Box> findByIdAndIsAvailable(long id, Boolean isAvailable) {
        return boxRepository.findByIdAndIsAvailable(id, isAvailable);
    }

    public void addCustomToCart(Box box, String userEmail) throws IOException, SQLException {
        box.setPrice(19.99f);
		box.setMadeByAdmin(false);
		box.setIsOpenBox(false);
		ClassPathResource resource = new ClassPathResource("static/images/Boxes/box_red2.png");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Blob blob = new SerialBlob(bytes);
		box.setImage(blob);

		Order cart = orderService.findByUserEmailAndIsOpen(userEmail, true).stream().findFirst().get();    
        cart.updateCart();
    }
    
    public void addChocolateToBox(Box box, Chocolate chocolate) {
        
        List<Chocolate> chocolates = box.getChocolates();
        chocolates.add(chocolate);
		box.setChocolates(chocolates);
	
    }

    public Boolean isBoxFull(Box box) { 
        return box.getChocolates().size() >= box.getSize();
    }

    public Box createBox(String name,float price, Blob image, Boolean madeByAdmin, List<Chocolate> chocolates, String userEmail) {
        Box box= new Box(name, price, image, madeByAdmin, chocolates);
		box.setIsOpenBox(true);
        box = boxRepository.save(box);
        orderService.addBoxToCart(userEmail, box);
        return box;
    }
    






}
