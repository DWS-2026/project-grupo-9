package es.codeurjc.web.dto;

public class IsOpenRequest {
    private Boolean isOpen;
    public IsOpenRequest() {
    }
    public IsOpenRequest(Boolean isOpen) {
        this.isOpen = isOpen;
    }
    public Boolean getIsOpen() {
        return isOpen;
    }
    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }
    @Override
    public String toString() {
        return "IsOpenRequest [isOpen=" + isOpen + "]";
    }
}
