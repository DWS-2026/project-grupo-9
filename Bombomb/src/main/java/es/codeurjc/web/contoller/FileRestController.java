package es.codeurjc.web.contoller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import es.codeurjc.web.dto.FileDTO;
import es.codeurjc.web.dto.FileMapper;
import es.codeurjc.web.model.File;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.FileService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1/files")
public class FileRestController {
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;


    @GetMapping("/{id}/media")
    public ResponseEntity<Resource> getFileMedia(@PathVariable long id, HttpServletRequest request) throws SQLException, IOException {
        Principal principal = request.getUserPrincipal();
        if(fileService.getFileIfOwnerOrAdmin(id, principal, principal.getName()) !=null){
            Resource resource = fileService.getFileResource(id, principal, principal.getName());

            MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok().contentType(mediaType).body(resource);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                
    }
    
    @GetMapping("/")
    public Collection<FileDTO> getAllFiles(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
        if(userService.isAdminRole(user)){
            return fileMapper.toDTOs(fileService.findAll());
        }
        return null; //no happen
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getFile(@PathVariable long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        File file = fileService.findById(id).orElseThrow();
        if(fileService.getFileIfOwnerOrAdmin(id, principal, principal.getName()) != null){
            return ResponseEntity.ok(fileMapper.toDTO(file));
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
