package com.grupo6.votingapp.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

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

    public byte[] downloadImage(String objectName) {
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, objectName));
        return blob.getContent();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // first check if the objectname already exists
        String originalFileName = file.getOriginalFilename();
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objName = System.currentTimeMillis() + "_" + fileName + extension;
        
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, objName));
        int index = 1;
        while (blob != null) {
            objName = System.currentTimeMillis() + "_" + fileName + "_" + index++ + extension;
            System.out.println("Object name already exists. Renaming it to " + objName + ".");
            blob = storage.get(BlobId.of(BUCKET_NAME, objName));
        }
        BlobId blobId = BlobId.of(BUCKET_NAME, objName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.create(blobInfo, file.getBytes());
            return objName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFile(String objectName) {
        BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
        storage.delete(blobId);
    }
}

