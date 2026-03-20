package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.Chocolate;
import es.codeurjc.web.model.Order;
import es.codeurjc.web.repository.BoxRepository;
import es.codeurjc.web.repository.ChocolateRepository;

public class BoxService {
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ChocolateRepository chocolateRepository;


    
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












}
