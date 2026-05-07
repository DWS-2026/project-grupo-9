package es.codeurjc.web.contoller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import es.codeurjc.web.model.File;
import es.codeurjc.web.service.FileService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;
    
    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> serveFile(@PathVariable long id,
            HttpServletRequest request) throws IOException {

        Resource resource = fileService.getFileResource(id, request.getUserPrincipal());

        if (resource == null) {
            Resource notFoundResource = fileService.getNotFoundImage().getBody();
            String contentType = fileService.getContentType(notFoundResource);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(notFoundResource);
        }

        String contentType = fileService.getContentType(resource);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(resource);
    }

    @GetMapping("/viewFile/{id}")
    public String viewFile(@PathVariable long id, Model model, HttpServletRequest request) {

        File file = fileService.getFileIfOwnerOrAdmin(id, request.getUserPrincipal());

        if (file == null) {
            return "redirect:/error/NotYourFile";
        }
        model.addAttribute("fileId", id);
        model.addAttribute("fileName", file.getOriginalName());

        return "viewFile";
    }

}
