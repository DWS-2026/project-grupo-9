package es.codeurjc.web.service;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.File;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class FileService {

    private final Tika tika= new Tika();
    private final List <String> allowedMimeTypes= List.of("application/pdf","image/jpeg","image/png","image/gif","image/jpg");
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private BoxService boxService;


    public ResponseEntity<Resource> getNotFoundImage() {
        ClassPathResource notFoundFile = new ClassPathResource("static/images/notFound.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(notFoundFile);
    }

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public Optional<File> findById(long id) {
        return fileRepository.findById(id);
    }

    // return true if the user is the owner (validation for admin in controller)
    public Boolean isFileOwner(File file, Principal principal) {
        if (principal == null) {
            return false;
        }
        String userEmail = principal.getName();
        return file.getUser().getEmail().equals(userEmail);
    }

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");

    public void uploadFile(MultipartFile originalFile, User user, Box box) throws IOException {

        Files.createDirectories(FILES_FOLDER);
        File file = new File();
        file.setOriginalName(originalFile.getOriginalFilename());

        file.setUser(user);
        fileRepository.save(file);

        box.setFile(file);
        boxService.save(box);
        Path filePath = FILES_FOLDER.resolve(file.getOriginalName());
        originalFile.transferTo(filePath);

	}
    public boolean isValidExtension(String filename){
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension=filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return List.of("pdf","jpg","jpeg","png","gif").contains(extension);

    }

    public boolean validateExtTika(MultipartFile file) throws IOException {
        String mimeType = tika.detect(file.getInputStream());
        String normalizedFilename = FilenameUtils.normalize(file.getOriginalFilename());

        return allowedMimeTypes.contains(mimeType) && normalizedFilename!=null;
    }
  
  
    public Resource getFileResource(long id, Principal principal) throws IOException {

        File file = getFileIfOwnerOrAdmin(id, principal);
        if (file == null) {
            return null;
        }
        Path filePath = Paths.get(System.getProperty("user.dir"), "files").resolve(file.getOriginalName());

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return null;
        }
        return resource;
    }

    public String getContentType(Resource resource) throws IOException {

        Path path = Paths.get(resource.getURI());
        String contentType = Files.probeContentType(path);

        return contentType != null ? contentType : "application/octet-stream";
    }

    public File getFileIfOwnerOrAdmin(long id, Principal principal) {
        File file = fileRepository.findById(id).orElse(null);
        if (file == null || !(isFileOwner(file, principal)|| userService.isAdminRole(userService.findByEmail(principal.getName()).orElseThrow()))) {
            return null;
        }
        return file;
    }

}