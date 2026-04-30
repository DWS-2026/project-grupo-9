package es.codeurjc.web.contoller;



import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.service.FileService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;


	@PostMapping("/new_file")
	public String postFile(@RequestParam MultipartFile file, Model model, HttpServletRequest request)
			throws IOException {

	    fileService.uploadFile(file,userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow());
        return "pruebaFile";
	}

    @GetMapping("/upload_file")
    public String getFile(){
        return "pruebaFile";
    }
    
}
