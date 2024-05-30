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
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class GcsService {

    private Storage storage;
    private static final String BUCKET_NAME = "images-aa-sic";

    public GcsService() throws IOException {
        this.storage = getStorage();
    }

    private static Storage getStorage() throws IOException{
        ClassPathResource resource = new ClassPathResource("aa-sic-424610-eea4d305377e.json");
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
            // System.out.println("Error downloading image: " + e.getMessage());
            throw new ImageServerException("Error downloading image: " + e.getMessage());
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
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // Adiciona um timestamp ao nome do arquivo para evitar duplicatas
        String objName = System.currentTimeMillis() + "_" + fileName + extension;
        
        // Acrescenta um número ao nome do arquivo se ele já existir, até que seja gerado um nome único
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, objName));
        int index = 1;
        while (blob != null) {
            objName = System.currentTimeMillis() + "_" + fileName + "_" + index++ + extension;
            System.out.println("Object name already exists. Renaming it to " + objName + ".");
            blob = storage.get(BlobId.of(BUCKET_NAME, objName));
        }

        BlobId blobId = BlobId.of(BUCKET_NAME, objName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        try {
            storage.create(blobInfo, file.getBytes());
            return objName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFile(String objectName) throws ImageServerException, ImageNotFoundException {
        BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
        try{
            Boolean res = storage.delete(blobId);
            if (!res) {
                throw new ImageNotFoundException("Image not found in Storage.");
            }
        } catch (StorageException e) {
            throw new ImageServerException("Error downloading image: " + e.getMessage());
        }
    }
}

