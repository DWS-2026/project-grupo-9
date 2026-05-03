package es.codeurjc.web.dto;
import java.time.LocalDate;
import java.util.List;


public record OrderDTO (long id, LocalDate date, float price, int amount, Boolean isOpen, UserEmailDTO user, List<BoxGetDTO> boxes) {
    
}
