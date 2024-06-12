package com.grupo6.votingapp.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.grupo6.votingapp.exceptions.imageStorage.ImageNotFoundException;
import com.grupo6.votingapp.exceptions.imageStorage.ImageServerException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

@Service
public class ImageService {

    private Storage storage;
    private static final String BUCKET_NAME = "images-aa-sic";

    @Value("${google.cloud.serviceAccount}")
    private String serviceAccount;

    @PostConstruct
    private void init() throws IOException {
        this.storage = getStorage(serviceAccount);
    }

    private static Storage getStorage(String serviceAccount) throws IOException {
        Assert.notNull(serviceAccount, "Service account path must not be null");
        ClassPathResource resource = new ClassPathResource(serviceAccount);
        InputStream inputStream = resource.getInputStream();
        String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        Credentials credentials = GoogleCredentials.fromStream(credentialsStream);
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    /**
     * Download an image from the Google Cloud Storage bucket.
     * @param objectName The name of the object to download.
     * @return The byte array of the image.
     * @throws IOException If the image could not be downloaded.
     */
    public byte[] downloadImage(String objectName) throws ImageServerException, ImageNotFoundException{
        try{
            Blob blob = storage.get(BlobId.of(BUCKET_NAME, objectName));
            if (blob == null) {
                throw new ImageNotFoundException("Image not found in Storage.");
            }
            return blob.getContent();
        } catch (StorageException e) {
            throw new ImageServerException("Error downloading image: " + e.getMessage());
        }
    }

    /**
     * Download an image from the Google Cloud Storage bucket, within a specified folder.
     * @param folderName
     * @param objectName
     * @return
     * @throws ImageServerException
     * @throws ImageNotFoundException
     */
    public byte[] downloadImage(String folderName, String objectName) throws ImageServerException, ImageNotFoundException{
        String fullObjectName = folderName == null || folderName.isEmpty() ? objectName : folderName + "/" + objectName;
        return downloadImage(fullObjectName);
    }

    /**
     * Upload an image to the Google Cloud Storage bucket, within a specified folder.
     * @param folderName Pode ser null ou vazio, caso não queira guardar em folder.
     * @param objectName
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadImage(String folderName, String objectName, MultipartFile file) throws IOException {
        
        String fullObjectName = folderName == null || folderName.isEmpty() ? objectName : folderName + "/" + objectName;

        String fileName = fullObjectName.substring(0, fullObjectName.lastIndexOf("."));
        String extension = fullObjectName.substring(fullObjectName.lastIndexOf("."));
        
        // Acrescenta um número ao nome do arquivo se ele já existir, até que seja gerado um nome único (pouco provável de acontecer)
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, fullObjectName));
        int index = 1;
        while (blob != null) {
            fullObjectName = fileName + "_" + index++ + '_' + System.currentTimeMillis() + extension;
            System.out.println("Object name already exists. Renaming it to " + fullObjectName + ".");
            blob = storage.get(BlobId.of(BUCKET_NAME, fullObjectName));
        }

        BlobId blobId = BlobId.of(BUCKET_NAME, fullObjectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        try {
            storage.create(blobInfo, file.getBytes());
            return fullObjectName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Upload an image to the Google Cloud Storage bucket.
     * @param file The image file to upload.
     * @return The name of the object in the bucket.
     * @throws IOException If the image could not be uploaded.
     */
    public String uploadImage(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        return uploadImage("", originalFileName, file);
    }


    public Map<String, String> uploadImages(List<MultipartFile> images) throws IOException {
        Map<String, String> uploadedImages = new HashMap<>();
        try {
            for(MultipartFile image : images) {
                String imageName = image.getOriginalFilename();
                String uploadedImageName = uploadImage(image);
                uploadedImages.put(imageName, uploadedImageName);
            }
            
            return uploadedImages;
        } catch (IOException e) {
            for(String uploadedImageName : uploadedImages.values()) {
                deleteFile(uploadedImageName);
            }
            throw e;
        }
    }

    public void deleteFile(String objectName) throws ImageServerException, ImageNotFoundException {
        BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
        try{
            boolean res = storage.delete(blobId);
            if (!res) {
                throw new ImageNotFoundException("Image not found in Storage.");
            }
        } catch (StorageException e) {
            throw new ImageServerException("Error deleting image: " + e.getMessage());
        }
    }
}

