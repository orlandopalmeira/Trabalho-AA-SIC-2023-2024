package com.grupo6.votingapp.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.services.GoogleCloudStorageService;

@RestController
@RequestMapping("/images")
public class ImageController {

    private GoogleCloudStorageService storageService;
    private static final String BUCKET_NAME = "images-aa-sic";

    public ImageController(GoogleCloudStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageName) {
        return storageService.downloadImage(BUCKET_NAME, imageName);
    }
}