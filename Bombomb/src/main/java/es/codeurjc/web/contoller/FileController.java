package es.codeurjc.web.contoller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.User;
import es.codeurjc.web.model.File;
import es.codeurjc.web.service.FileService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    /*
     * @PostMapping("/new_file")
     * public String postFile(@RequestParam MultipartFile file, Model model,
     * HttpServletRequest request)
     * throws IOException {
     * 
     * fileService.uploadFile(file,userService.findByEmail(request.getUserPrincipal(
     * ).getName()).orElseThrow());
     * return "pruebaFile";
     * }
     */

    //moverlo a box controller cuando se añada una caja personalizada
    /*@PostMapping("/new_file")
    public String postFile(@RequestParam MultipartFile file, Model model, HttpServletRequest request)
            throws IOException {

        String filename = file.getOriginalFilename();
        if (filename == null || !fileService.isValidExtension(filename)) {
            return "redirect:/error/invalidFile";
        }
        try (InputStream is = file.getInputStream()) {
            if (fileService.validateExtTika(is)) {
           
                User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
                fileService.uploadFile(file, user);

                return "pruebaFile";
            } else {
                return "redirect:/error/invalidFile";
            }
        }
    }*/


    /*@GetMapping("/upload_file")
    public String getFile(){
        return "pruebaFile";
    }*/
    
    @GetMapping("/upload_file")
    public String getFile() {
        return "pruebaFile";
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> serveFile(@PathVariable long id,
            HttpServletRequest request) throws IOException {

        Resource resource = fileService.getFileResource(id, request.getUserPrincipal(), request.getUserPrincipal().getName());

        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType = fileService.getContentType(resource);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(resource);
    }

    @GetMapping("/viewFile/{id}")
    public String viewFile(@PathVariable long id, Model model, HttpServletRequest request) {

        String email = request.getUserPrincipal().getName();
        File file = fileService.getFileIfOwnerOrAdmin(id, request.getUserPrincipal(), email);

        if (file == null) {
            return "redirect:/error/NotYourFile";
        }
        model.addAttribute("fileId", id);
        model.addAttribute("fileName", file.getName());

        return "viewFile";
    }

}
