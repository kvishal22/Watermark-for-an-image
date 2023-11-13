package com.kanna.imageMerge.controller;

import com.kanna.imageMerge.entity.Image;
import com.kanna.imageMerge.service.ImageService;
import jakarta.persistence.GeneratedValue;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file) throws IOException {
        try {
            log.info("Uploaded File Size: {} ", file.getSize());
            imageService.uploadImage(file);
            return ResponseEntity.ok("file uploaded");
        }catch (IOException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }
    @GetMapping("/all/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id){
        byte[] imageData=imageService.downloadImage(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
    @GetMapping("/all")
        public List<Image> getAll(){
            return imageService.getAll();
        }
        @DeleteMapping("/{id}")
    public String del(@PathVariable Long id){
        return imageService.delete(id);
        }
    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return imageService.oneImage(id);
    }
        @PostMapping("/icon")
    public ResponseEntity<String> saveImage(@RequestParam("file")MultipartFile file) throws IOException {
        imageService.saveImagewithIcon(file);
        return ResponseEntity.ok().body("saved");
        }
        @GetMapping("/getIds")
    public void some(){
        imageService.some();
        }

    }

