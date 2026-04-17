package es.codeurjc.web.dto;
import java.time.LocalDate;

public record OrderDTO (long id, LocalDate date, float price, int amount, boolean isOpen) {
    
}
