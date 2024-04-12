package com.image.process.controller;

import com.image.process.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.image.process.utill.CommonUtil.UPLOAD_DIRECTORY;

@RestController
@RequestMapping("/image")
@Slf4j
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/color-change")
    public ResponseEntity<String> imageColorChange(@RequestBody MultipartFile file) {
//        This Approach not accept Big size image
//        Make Sure you have increase the file size
        if(file.isEmpty()) {
            throw new RuntimeException("File is Empty");
        }

        String fileName = file.getOriginalFilename();
        log.info("File name : {}",fileName);

        String contentType = file.getContentType();
        log.info("File Type : {}", contentType);

        try {
            File createFile = File.createTempFile("upload", "-" + file.getOriginalFilename());


            Path filePath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());

            Files.createDirectories(filePath.getParent());

            file.transferTo(filePath);
            log.info("File Save Successfully...");
            log.info("File Path with name : {}", filePath.toAbsolutePath());

            log.info("Image Processing Start...");
            imageService.imageColorChangeGrayToPurple(filePath.getFileName().toString());
            log.info("Image Processing End...");

        } catch (Exception ex) {
            log.error("Exception Message : {}", ex.getMessage());
        }

        return ResponseEntity.ok("Successfully Hit the endpoint");
    }

}
