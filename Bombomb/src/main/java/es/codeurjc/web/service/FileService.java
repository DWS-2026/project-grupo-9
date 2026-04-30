package es.codeurjc.web.service;

import es.codeurjc.web.model.File;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.FileRepository;
import es.codeurjc.web.repository.ImageRepository;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.repository.UserRepository;


import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;


   /* public ResponseEntity<Object> getFile(File file) throws SQLException {
        
        InputStreamResource pdfFile = new InputStreamResource(file.getPdfFile().getBinaryStream());
        MediaType mediaType = MediaTypeFactory.getMediaType(pdfFile).orElse(MediaType.APPLICATION_PDF);
        
        return ResponseEntity.ok().contentType(mediaType).body(pdfFile);
    }*/

    public ResponseEntity<Object> getNotFoundImage() {
        ClassPathResource notFoundFile = new ClassPathResource("static/files/notFound.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(notFoundFile);
    }

    public List<File> findAll(){
        return fileRepository.findAll();
    }

    public Optional<File> findById(long id){
        return fileRepository.findById(id);
    }
    //return true if the user is the owner (validation for admin in controller)
    public Boolean isPdfFileOwner(File file, Principal principal) {
        if (principal == null) {
            return false;
        }
        String userEmail = principal.getName();
        return file.getUser().getEmail().equals(userEmail);
    }

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");

	public void uploadFile(MultipartFile originalFile, User user)
			throws IOException {

		Files.createDirectories(FILES_FOLDER);
		File file = new File();
        file.setOriginalName(originalFile.getOriginalFilename());
        file.setUser(user);
        fileRepository.save(file);

        file.setName("file"+file.getId()+FilenameUtils.getExtension(originalFile.getOriginalFilename()));

		Path filePath = FILES_FOLDER.resolve(file.getName());
		//
		originalFile.transferTo(filePath);

	}
}