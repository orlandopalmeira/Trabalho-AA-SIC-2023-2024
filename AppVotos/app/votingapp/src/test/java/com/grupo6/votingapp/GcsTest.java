package com.grupo6.votingapp;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class GcsTest {

    // @Autowired
    // private GcsService gcsService;

    // @Test
    // public void testDownload() {
    //     // Assuming MyService has a method called someMethod() that you want to test
    //     String imageName = "kitten.png";
    //     byte[] result = gcsService.downloadImage(imageName);
    //     // Write the image to a file
    //     try {
    //         String filePath = "image-ret.png";
    //         FileOutputStream fos = new FileOutputStream(filePath);
    //         fos.write(result);
    //         fos.close();
    //     } catch (IOException e) {
    //         // Handle the exception
    //         e.printStackTrace();
    //     }

    // }

    // @Test
    // public void testDownloadFromFolder() {
    //     // Assuming MyService has a method called someMethod() that you want to test
    //     String folder = "profile";
    //     String imageName = "image.png";
    //     String path = folder + "/" + imageName;
    //     byte[] result = gcsService.downloadImage(path);
    //     // Write the image to a file
    //     try {
    //         String filePath = "image-ret.png";
    //         FileOutputStream fos = new FileOutputStream(filePath);
    //         fos.write(result);
    //         fos.close();
    //     } catch (IOException e) {
    //         // Handle the exception
    //         e.printStackTrace();
    //     }

    // }

    // @Test
    // public void TestUpload() {
    //     String imageName = "test1.png";
    //     String filePath = "/home/pedro/Trabalho-AA-SIC-2023-2024/AppVotos/app/votingapp/src/test/java/com/grupo6/votingapp/test1.png";
    //     MultipartFile file = null;
    //     file = new MockMultipartFile("test1.png", new FileInputStream(filePath));
    //     try {
    //         gcsService.uploadImage(filePath);
    //     } catch (IOException e) {
    //         // Handle the exception
    //         e.printStackTrace();
    //     }
    // }
}
