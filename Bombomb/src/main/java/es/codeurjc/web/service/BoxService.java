package es.codeurjc.web.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.BoxRepository;

@Service
public class BoxService {
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ChocolateService chocolateService;



    
    public List<Box> findAll() {
        return boxRepository.findAll();
    }
    public Optional<Box> findById(Long id) {
        return boxRepository.findById(id);
    }
    public Box save(Box box) {
        return boxRepository.save(box);
    }
    public List<Box> findByMadeByAdminAndIsOpenBoxAndIsAvailable(Boolean madeByAdmin, Boolean isOpenBox, Boolean isAvailable) {
        return boxRepository.findByMadeByAdminAndIsOpenBoxAndIsAvailable(madeByAdmin, isOpenBox, isAvailable);
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
    public Optional<Box> findByIdAndIsAvailableAndMadeByAdmin(long id, Boolean isAvailable, Boolean madeByAdmin) {
        return boxRepository.findByIdAndIsAvailableAndMadeByAdmin(id, isAvailable, madeByAdmin);
    }
    public Collection<Box> findByMadeByAdminAndIsAvailable(Boolean madeByAdmin, Boolean isAvailable){
        return boxRepository.findByMadeByAdminAndIsAvailable(madeByAdmin, isAvailable);
    }

    public List<Box> findOwnedAndAdminBoxes(User user) {
        List<Box> ownedBoxes = boxRepository.findByOrdersUserEmail(user.getEmail());
        List<Box> adminBoxes = boxRepository.findByMadeByAdminAndIsAvailable(true, true);
        ownedBoxes.addAll(adminBoxes);
        return ownedBoxes;
    }

    public boolean hasPermission(User user, Box box, Boolean verifyAdmin) {
        if (user.isThisRole("ADMIN") && verifyAdmin) {
            return true;
        }
        List<Box> ownedBoxes = boxRepository.findByOrdersUserEmail(user.getEmail());
        return ownedBoxes.contains(box);
    }

    public void delete(Box box) {
        box.setIsAvailable(false);
                List <Order> orders = orderService.findByBoxesAndIsOpen(box, true);
                for(Order order : orders){
                    order.removeBox(box);
                }
                boxRepository.save(box);
    }
    
    public void addCustomToCart(Box box, String userEmail) throws IOException, SQLException {
		ClassPathResource resource = new ClassPathResource("static/images/Boxes/box_red2.png");
		byte[] bytes = resource.getInputStream().readAllBytes();
		Image blob = new Image(new SerialBlob(bytes), userEmail);
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

    public Box createBox(String name,float price, Image image, Boolean madeByAdmin, List<Chocolate> chocolates, String userEmail) {
        Box box= new Box(name, price, image, madeByAdmin, chocolates);
		box.setIsOpenBox(true);
        box = boxRepository.save(box);
        orderService.addBoxToCart(userEmail, box);
        return box;
    }
    
    public void createApiBox(Box box, User user) {
        box.setIsOpenBox(true);
        box.setChocolates(new ArrayList<>());
        box.setMadeByAdmin(user.getRoles().contains("ADMIN"));
        boxRepository.save(box);
        orderService.addBoxToCart(user.getEmail(), box);
    }



    public void randomizeBox(Box box) {
        List<Chocolate> chocolates = box.getChocolates();
        if(chocolates != null){
		    chocolates.clear(); //empty the box before filling it with random chocolates
        }else{
            chocolates = new ArrayList<>();
        }
		//if the box is empty, fill it with random chocolates, if not,emty it and fill it with random chocolates
		int totalSize = chocolateService.findByIsAvailable(true).size();
		int boxSize = box.getSize();

		for(int i=0; i<boxSize; i++){
			int randomIndex = (int) (Math.random() * totalSize);
			chocolates.add(chocolateService.findByIsAvailable(true).get(randomIndex));
		}
		boxRepository.save(box); 
    }

}
