package com.kanna.imageMerge.service;

import com.kanna.imageMerge.entity.Image;
import com.kanna.imageMerge.repo.ImageRepo;
import com.kanna.imageMerge.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

    private final ImageRepo imageRepo;

    public ImageService(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    public String  uploadImage(MultipartFile file) throws IOException {

        imageRepo.save(Image.builder()
                        .imageData(ImageUtil.compressImage(file.getBytes()))
                .build());

            return "file uploaded successfully : " + file.getOriginalFilename();
        }
    public byte[] downloadImage(Long id) {
        Optional<Image> dbImageData = imageRepo.findById(id);
       if(dbImageData.isEmpty()){
            throw new IllegalArgumentException();
        }
         return ImageUtil.decompressImage(dbImageData.get().getImageData());
    }
    public List<Image> getAll(){
        return imageRepo.findAll();
    }
    public String delete(Long id){
        imageRepo.deleteById(id);
        return "deleted";
    }
    public ResponseEntity<byte[]>  oneImage(Long id){
        Optional<Image> optionalImage = imageRepo.findById(id);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(image.getImageData().length);
            return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public void saveImagewithIcon(MultipartFile file) throws IOException {

        byte[] orgImageData = file.getBytes();
        byte[] modifiedImageData = addPlayIcon(orgImageData);


        log.info("modified Image data {}",modifiedImageData.length);

       imageRepo.save(Image.builder()
                .imageData(modifiedImageData)
                .build());
    }

    private byte[] addPlayIcon(byte[] orgImageData) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(orgImageData);
        BufferedImage orgImage = ImageIO.read(inputStream);

        InputStream playIconStream = getClass().getResourceAsStream("/images/playIcon.png");
        assert playIconStream != null;
        BufferedImage playIcon = ImageIO.read(playIconStream);

        BufferedImage resizedPlayIcon = Thumbnails.of(playIcon)
                .size(40, 40)
                .asBufferedImage();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(orgImage)
                .size(orgImage.getWidth(),orgImage.getHeight())
                .outputQuality(1.0)
                .outputFormat("png")
                .watermark(Positions.BOTTOM_LEFT, resizedPlayIcon, 1.0f)

                .toOutputStream(outputStream);

        log.info("output stream  {}",outputStream.size());

       return outputStream.toByteArray();

    }
    public void some(){
        imageRepo.findAll().stream().map(Image::getId).forEach(id->log.info("id {}",id));
    }

}
