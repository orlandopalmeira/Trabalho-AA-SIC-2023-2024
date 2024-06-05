package com.grupo6.votingapp.controllers;

import java.io.IOException;
// import java.io.InputStream;

// import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {

    private ImageService storageService;

    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
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
        }
    }

    // Posting an image to the bucket
    // @PostMapping(value = "/{imageName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public void postImage(@PathVariable String imageName, @RequestPart MultipartFile file) {
    //     storageService.uploadImage(BUCKET_NAME, imageName, file);
    // }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleImageUpload(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty() || file.getContentType().startsWith("image") == false) {
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