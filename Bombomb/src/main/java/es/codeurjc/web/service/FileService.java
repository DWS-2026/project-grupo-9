package es.codeurjc.web.service;

import es.codeurjc.web.model.Box;
import es.codeurjc.web.model.File;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.FileRepository;
import es.codeurjc.web.repository.ImageRepository;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.web.service.UserService;


@Service
public class FileService {

    private final Tika tika= new Tika();
    private final List <String> allowedMimeTypes= List.of("application/pdf","image/jpeg","image/png","image/gif","image/jpg");
    /* 
    private final Map<String, String> signatures = Map.of(
    "gif", "474946383761",
    "pdf", "255044462D",
    "jpg", "FFD8FFE0",
    "jpeg", "FFD8FFE0",
    "png", "89504E470D0A1A0A"
    );*/
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private UserService userService;

    /*
     * public ResponseEntity<Object> getFile(File file) throws SQLException {
     * 
     * InputStreamResource pdfFile = new
     * InputStreamResource(file.getPdfFile().getBinaryStream());
     * MediaType mediaType =
     * MediaTypeFactory.getMediaType(pdfFile).orElse(MediaType.APPLICATION_PDF);
     * 
     * return ResponseEntity.ok().contentType(mediaType).body(pdfFile);
     * }
     */

    public ResponseEntity<Object> getNotFoundImage() {
        ClassPathResource notFoundFile = new ClassPathResource("static/files/notFound.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(notFoundFile);
    }

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public Optional<File> findById(long id) {
        return fileRepository.findById(id);
    }

    // return true if the user is the owner (validation for admin in controller)
    public Boolean isPdfFileOwner(File file, Principal principal) {
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

        file.setName("file" + file.getId() + "." + FilenameUtils.getExtension(originalFile.getOriginalFilename()));
        fileRepository.save(file);

        box.setFile(file);
        Path filePath = FILES_FOLDER.resolve(file.getName());
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
        FilenameUtils.normalize(file.getOriginalFilename());
        return allowedMimeTypes.contains(mimeType);
    }
   /* public boolean validateExtTika(InputStream file) throws IOException {
        String mimeType = tika.detect(file);
        return allowedMimeTypes.contains(mimeType);
    }*/
 
    /* 
    public String fromBytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public boolean validateExtensionFromBytes(InputStream file) throws IOException {
        byte[] header = new byte[8];
        int read = file.read(header);
        if (read < 4) {
            return false;
        } else {
            String hexHeader = fromBytesToHex(header);

            // search for valid signature in the list of signatures
            for (Map.Entry<String, String> entry : signatures.entrySet()) {
                if (hexHeader.startsWith(entry.getValue())) {
                    return true;
                }
            }
            return false;
        }
    }

    public Boolean isValidExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return signatures.containsKey(extension);
    }*/
    

  
    public Resource getFileResource(long id, Principal principal, String email) throws IOException {

        File file = getFileIfOwnerOrAdmin(id, principal, email);
        if (file == null) {
            return null;
        }
        Path filePath = Paths.get(System.getProperty("user.dir"), "files").resolve(file.getName());

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

    public File getFileIfOwnerOrAdmin(long id, Principal principal, String email) {
        File file = fileRepository.findById(id).orElse(null);
        if (file == null || !(isPdfFileOwner(file, principal)|| userService.isAdminRole(userService.findByEmail(email).orElseThrow()))) {
            return null;
        }
        return file;
    }

}