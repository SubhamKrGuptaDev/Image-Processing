package com.image.process.controller;

import com.image.process.exception.ColorNotSupportException;
import com.image.process.exception.FileException;
import com.image.process.exception.PathException;
import com.image.process.service.ImageService;
import com.image.process.utill.CommonUtil;
import com.image.process.utill.ResultColor;
import com.image.process.utill.TargetColor;
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
    public ResponseEntity<String> imageColorChange(@RequestBody MultipartFile file,
                                                   @RequestParam String targetColor,
                                                   @RequestParam String resultColor) {
//        This Approach not accept Big size image
//        Make Sure you have increase the file size
        if (file.isEmpty()) {
            throw new FileException();
        }

        if (!TargetColor.GRAY.toString().equals(targetColor)) {
            throw new ColorNotSupportException("Target Color Not Supported");
        }

        if (!ResultColor.PURPLE.toString().equals(resultColor)) {
            throw new ColorNotSupportException("Result Color Not Supported");
        }

        String fileName = file.getOriginalFilename();
        log.info("File name : {}",fileName);

        String contentType = file.getContentType();
        log.info("File Type : {}", contentType);

        try {

            Path filePath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());

            Files.createDirectories(filePath.getParent());

            file.transferTo(filePath);
            log.info("File Save Successfully...");
            log.info("File Path with name : {}", filePath.toAbsolutePath());

            log.info("Image Processing Start...");
            String resultFileName = imageService.imageColorChangeGrayToPurple(filePath.getFileName().toString());
            log.info("Image Processing End...");
            return ResponseEntity.ok("Absolute File Path : " + resultFileName);
        } catch (Exception ex) {
            log.error("Exception Message : {}", ex.getMessage());
            throw new PathException("Path Exception Message : " + ex.getMessage());
        }

    }

}
