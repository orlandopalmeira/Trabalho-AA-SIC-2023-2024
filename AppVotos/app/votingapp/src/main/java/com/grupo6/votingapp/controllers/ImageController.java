package com.grupo6.votingapp.controllers;

import java.io.IOException;
// import java.io.InputStream;

// import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6.votingapp.exceptions.imageStorage.ImageNotFoundException;
import com.grupo6.votingapp.exceptions.imageStorage.ImageServerException;
import com.grupo6.votingapp.services.ImageService;
import com.grupo6.votingapp.services.VotingService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {

    private ImageService storageService;
    private VotingService votingService;
    private CheckTokenMiddlewares authMiddlewares;

    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> getImage(@PathVariable String imageName, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                Long votingId = votingService.votingIdWithImage(userId, imageName); //! NÃO TESTADO verifica se a imagem pertence a uma votação acessível ao utilizador
                if (votingId == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                byte[] imageBytes = storageService.downloadImage(imageName);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentLength(imageBytes.length);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } catch (ImageServerException e) {
                System.out.println(e.getMessage() + " Image name: " + imageName);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } catch (ImageNotFoundException e) {
                System.out.println(e.getMessage() + " Image name: " + imageName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @GetMapping(value = "/avatar/{avatarName}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> getAvatar(@PathVariable String avatarName, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                byte[] imageBytes = storageService.downloadImage("avatar", avatarName);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentLength(imageBytes.length);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } catch (ImageServerException e) {
                System.out.println(e.getMessage() + " Image name: " + avatarName);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } catch (ImageNotFoundException e) {
                System.out.println(e.getMessage() + " Image name: " + avatarName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    // Posting an image to the bucket
    // @PostMapping(value = "/{imageName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public void postImage(@PathVariable String imageName, @RequestPart MultipartFile file) {
    //     storageService.uploadImage(BUCKET_NAME, imageName, file);
    // }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleImageUpload(@RequestParam("image") MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) contentType = "";
        if (file.isEmpty() || !contentType.startsWith("image")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a valid image to upload.");
        }
        try {
            String objName = storageService.uploadImage(file);
            return ResponseEntity.ok("Image uploaded successfully: " + objName + ".");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        }
    }
}