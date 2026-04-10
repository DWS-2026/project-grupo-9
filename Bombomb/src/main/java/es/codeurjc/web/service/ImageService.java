package es.codeurjc.web.service;

import java.sql.Blob;
import java.sql.SQLException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.codeurjc.web.service.ImageService;

@Service
public class ImageService {
    public ResponseEntity<Object> getImage(Blob image) throws SQLException {
        InputStreamResource imageFile = new InputStreamResource(image.getBinaryStream());
        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

    public ResponseEntity<Object> getNotFoundImage() {
        ClassPathResource notFoundImage = new ClassPathResource("static/images/notFound.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(notFoundImage);
    }
}
